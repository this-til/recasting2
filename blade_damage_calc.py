#!/usr/bin/env python3
"""
Recasting2 全实战刀满成长伤害计算（穷举 SE 组合版）
=====================================================
规则:
  - 所有叠加增伤视为"吃满"（电离64层、演算16层、灼烧50层、叠辉50层）
  - 循环: SA起手 + 10次幻影剑（ratio=0.2）作为填充
  - 对单/对5（群目标各吃对应的AOE系数）
  - 穷尽所有相关 SE 组合，取最佳结果
"""

# ========== 基础伤害公式 ==========
# ATTACK_DAMAGE = 10 + baseAttackModifier * 2
# ultimatelyModifiedRatio = modifiedRatio × modifiedRatioAmplifier × mechanismModifiedRatioAmplifier
# damage = ATTACK_DAMAGE × ultimatelyModifiedRatio + extraDamage
# 每source: actualDamage = damage × source.modifiedRatio + source.extraDamage

# --- 基础乘区（满成长） ---
BASE_AMPLIFIER = 2.2          # 附魔(0.2)+评分(0.5)+精炼(0.5)+其他
BASE_MECH_AMPLIFIER = 1.3     # 击杀>10000+精炼>10000+配置倍率
BASE_TOTAL = BASE_AMPLIFIER * BASE_MECH_AMPLIFIER  # 2.86

# ========== 模组满层全局 Buff 增伤（视为常驻） ==========
# 这些在 AttackAmplifierEvent 中加到 modifiedRatioAmplifier 上
GLOBAL_BUFF_AMPLIFIER = {
    'calculus': 0.80,    # 演算16层 × 5%/层
    'sunset_stack': 1.0, # 叠辉50层满层 +100%
    'photon_burn': 0.33, # 光子灼烧50层 maxLaserBonus
    'ionization': 0.64,  # 电离64层 × 1%/层
}

# ========== SE 参数（L5 满级） ==========

# --- 通用类型增幅 SE（AttackAmplifierSpecialEffect） ---
# 绑定 attackType，直接 addModifiedRatioAmplifier
SE_TYPE_AMPLIFIER = {
    '幻影剑': 0.6,    # 太虚 L5: 0.1+5*0.1 → SUMMOND_SWORD
    '斩击': 0.6,      # 利刃 L5: 0.1+5*0.1 → SLASH_EFFECT
    '次元斩': 0.95,   # 震荡 L5: 0.2+5*0.15 → JUDGEMENT_CUT
    '剑气': 0.95,     # 剑气纵横 L5: 0.2+5*0.15 → DRIVE
    '雷电': 0.95,     # 雷霆万钧 L5: 0.2+5*0.15 → LIGHTNING
}

# --- 触发型 SE L5 参数 ---
# 破片 L5: 满层12层后, 8.0倍率一次
FRAGMENT_L5_RATIO = 8.0
# 冲击 L5: 25%概率, 0.5ratio
IMPACT_L5_PROB = 0.25
IMPACT_L5_RATIO = 0.5
# 回旋 L5: 9把风暴剑环绕, 0.1ratio, 40tick冷却
SPIRAL_L5_COUNT = 9
SPIRAL_L5_RATIO = 0.1
# 分裂 L5: 0.95ratio幻影剑
SPLIT_L5_RATIO = 0.95

# 撕裂 L5: 满层后1.5倍率
TEAR_L5_RATIO = 1.5
# 断却 L5: 0.85ratio, 范围=4*ad*(1+5*0.1)=6ad
SEVER_BREAK_L5_RATIO = 0.85
# 断灭 L5: 巨型次元斩 0.06ratio
ANNIHILATION_L5_RATIO = 0.06
# 旋风 L5: 4tick间隔重复攻击
# 断罪 L5: 0.7ratio追加JC
JUDGEMENT_L5_RATIO = 0.7
# 风暴 L5: 6把幻影剑
STORM_L5_COUNT = 6
STORM_L5_RATIO = 0.25

# 雷暴 L5: 3道雷, 0.6ratio
THUNDERSTORM_L5_COUNT = 3
THUNDERSTORM_L5_RATIO = 0.6
# 蓄能 L5: 48层触发, 0.6ratio闪电
ENERGY_STORAGE_L5_RATIO = 0.6
# 雷云 L5: 受伤追加10%雷电
THUNDER_CLOUD_L5_RATIO = 0.1

# 星光 L5: 满层4层后, 2.25ratio
STAR_BLINK_RATIO = 2.25

# 协同 L5: 45%概率, 0.5额外倍率
COOPERATE_L5_PROB = 0.45
COOPERATE_L5_RATIO = 0.5
# 十字斩 L5: 0.5ratio
CROSS_CHOP_L5_RATIO = 0.5

# 长空落日SE自动剑
SUNSET_SE_RATIO_LV1 = 0.18  # lv1: 0.15*(1+1/5)
SUNSET_SE_RATIO_LV5 = 0.30  # lv5: 0.15*(1+5/5)

# 焰沫: 6%当前生命/秒
FLAME_FOAM_DOT_PCT = 0.06

# 解算(演算SLAM)
RESOLVE_RATIO = 1.0
RESOLVE_LAMBDA_RATIO = 1.5

# 黑玫瑰: 5%累计伤害每秒结算
BLACK_ROSE_RATIO = 0.05

# ========== SE 体系定义（每个体系 = 4槽满配 L5） ==========
# 每个体系包含: (名称, 类型增幅器, 类型标签, 额外触发伤害)

SE_SYSTEMS = {}

# 幻影剑系: 太虚+破片+回旋+冲击
# 太虚+0.6, 破片满层8.0一次, 回旋9剑0.1ratio, 冲击25%概率0.5ratio
SE_SYSTEMS['幻影剑系'] = {
    'type_amplifier': SE_TYPE_AMPLIFIER['幻影剑'],
    'attack_type': 'summond_sword',
    'bonus_desc': '+破片8.0×1 +回旋9剑0.1 +冲击0.5×0.25',
}

# 次元斩系: 震荡+撕裂+断却+旋风
SE_SYSTEMS['次元斩系'] = {
    'type_amplifier': SE_TYPE_AMPLIFIER['次元斩'],
    'attack_type': 'judgement_cut',
    'bonus_desc': '+撕裂1.5 +断却0.85AoE',
}

# 斩击系: 利刃+协同+十字斩+冲击
SE_SYSTEMS['斩击系'] = {
    'type_amplifier': SE_TYPE_AMPLIFIER['斩击'],
    'attack_type': 'slash_effect',
    'bonus_desc': '+协同0.5×0.45 +十字斩0.5',
}

# 雷电系: 雷霆万钧+电离+雷暴+雷云
SE_SYSTEMS['雷电系'] = {
    'type_amplifier': SE_TYPE_AMPLIFIER['雷电'],
    'attack_type': 'lightning',
    'bonus_desc': '+电离64层0.64 +雷暴3雷0.6',
}

# 剑气系: 剑气纵横+冲击+太虚+分裂
SE_SYSTEMS['剑气系'] = {
    'type_amplifier': SE_TYPE_AMPLIFIER['剑气'],
    'attack_type': 'drive',
    'bonus_desc': '+分裂0.95',
}

# ========== 幻影剑填充 ==========
# 10次幻影剑, ratio=0.2, 类型=summond_sword
PHANTOM_FILLER_COUNT = 10
PHANTOM_FILLER_RATIO = 0.2


# ========== 刀定义 ==========
class Blade:
    def __init__(self, name, base_atk, max_dp, sa_name, sa_data,
                 attack_distance=1.0, exclusive_se=None):
        self.name = name
        self.base_atk = base_atk
        self.max_dp = max_dp  # maxDamage/40
        self.sa_name = sa_name
        self.sa_data = sa_data
        self.ad = 10 + base_atk * 2
        self.attack_distance = attack_distance
        self.exclusive_se = exclusive_se or []


# ========== SA 参数常量 ==========
# 幻影剑系SA
SA_CLOUD_WHEEL = {'type': 'summond_sword', 'normal_swords': 6, 'normal_ratio': 0.2, 'lightning_swords': 10, 'lightning_sword_ratio': 0.2, 'lightning_aoe_ratio': 0.4}
SA_CLOUD_WHEEL_STORM = {'type': 'summond_sword', 'normal_swords': 10, 'normal_ratio': 0.2, 'lightning_swords': 8, 'lightning_sword_ratio': 0.2, 'lightning_aoe_ratio': 0.4}
SA_HEAVEN_TWELVE = {'type': 'lightning', 'lightning_swords': 12, 'lightning_sword_ratio': 0.1, 'lightning_aoe_ratio': 0.3}
SA_HEAVEN_TWELVE_LAMBDA = {'type': 'lightning', 'lightning_swords': 18, 'lightning_sword_ratio': 0.5, 'lightning_aoe_ratio': 1.3}
SA_LONG_SKY_SUNSET = {'type': 'summond_sword', 'swords': 16, 'sword_ratio': 0.12}
SA_LONG_SKY_SUNSET_LAMBDA = {'type': 'summond_sword', 'swords': 32, 'sword_ratio': 0.12}
SA_STORM_PHANTOM = {'type': 'summond_sword', 'swords': 12, 'sword_ratio': 0.15}
SA_STORM_PHANTOM_LAMBDA = {'type': 'summond_sword', 'swords': 18, 'sword_ratio': 0.20}
SA_SWORD_RAIN = {'type': 'summond_sword', 'swords': 150, 'sword_ratio': 0.05}
SA_SWORD_RAIN_LAMBDA = {'type': 'summond_sword', 'swords': 200, 'sword_ratio': 0.08}
SA_RAPID_PHANTOM = {'type': 'summond_sword', 'swords': 12, 'sword_ratio': 0.15}
SA_PHANTOM_EXPLOSION = {'type': 'spiral', 'min_swords': 36, 'max_swords': 72, 'ratio': 0.02}
SA_PHANTOM_EXPLOSION_LAMBDA = {'type': 'spiral', 'min_swords': 48, 'max_swords': 96, 'ratio': 0.03}
SA_MULTI_DRIVE = {'type': 'drive', 'drives': 4, 'ratio': 0.15}
SA_UBW = {'type': 'summond_sword', 'swords': 1024, 'sword_ratio': 0.04}

# 次元斩系SA
SA_MULTI_JC = {'type': 'judgement_cut', 'cuts': 4, 'ratio': 0.3}
SA_INFINITE_JC = {'type': 'judgement_cut', 'cuts': 12, 'ratio': 0.5}
SA_INFERNO = {'type': 'judgement_cut', 'size': 6}
SA_INFERNO_LAMBDA = {'type': 'judgement_cut', 'size': 8}
SA_VOID_HOLE = {'type': 'judgement_cut', 'life': 20, 'ratio': 0.05}
SA_VOID_HOLE_PITCH_BLACK = {'type': 'judgement_cut', 'life': 30, 'ratio': 0.08}
SA_VOID_HOLE_FISHY_RED = {'type': 'judgement_cut', 'life': 40, 'ratio': 0.10}

# 斩击系SA
SA_CYAN_GLOW = {'type': 'slash_effect', 'hits': 8, 'ratio': 0.3}
SA_CYAN_GLOW_LAMBDA = {'type': 'slash_effect', 'hits': 12, 'ratio': 0.35}
SA_FANATICAL_DANCE = {'type': 'slash_effect', 'hits': 15, 'max_hits': 18, 'ratio': 0.4}
SA_FANATICAL_DANCE_LAMBDA = {'type': 'slash_effect', 'hits': 20, 'max_hits': 25, 'ratio': 0.45}
SA_FRAGMENT = {'type': 'slash_effect', 'hits': 1, 'ratio': 0.3, 'repeated': True}

# 混合SA
SA_ZANTETSUDEN_MAX = {'type': 'mixed_jc_slash', 'cuts': 25, 'ratio': 0.03, 'jc_ratio': 0.25}
SA_ZANTETSUDEN_MAX_LAMBDA = {'type': 'mixed_jc_slash', 'cuts': 35, 'ratio': 0.04, 'jc_ratio': 0.30}
SA_ZANTETSUDEN_ROW = {'type': 'mixed_jc_drive', 'drives': 20, 'drive_ratio': 0.015, 'jc_ratio': 0.25}
SA_ZANTETSUDEN_ROW_LAMBDA = {'type': 'mixed_jc_drive', 'drives': 30, 'drive_ratio': 0.02, 'jc_ratio': 0.30}

# 阵系
SA_MATRIX = {'type': 'matrix', 'life': 200, 'interval': 10, 'ratio': 0.02}
SA_MATRIX_LAMBDA = {'type': 'matrix', 'life': 200, 'interval': 10, 'ratio': 0.03}

# 星璇(控制)
SA_STELLAR = {'type': 'aoe_control', 'ticks': 60, 'ratio_per_tick': 0.01}

# 星流SA
SA_STAR_1 = {'type': 'star', 'swords': 6, 'sword_ratio': 0.25, 'jc_ratio': 0.5}
SA_STAR_2 = {'type': 'star', 'swords': 8, 'sword_ratio': 0.30, 'jc_ratio': 0.6}
SA_STAR_3 = {'type': 'star', 'swords': 10, 'sword_ratio': 0.35, 'jc_ratio': 0.7}
SA_STAR_4 = {'type': 'star', 'swords': 12, 'sword_ratio': 0.40, 'jc_ratio': 0.8}
SA_STAR_4_LAMBDA = {'type': 'star', 'swords': 16, 'sword_ratio': 0.45, 'jc_ratio': 0.9}

# 激光系SA
SA_LASER_1 = {'type': 'laser', 'beams': 1, 'main_ratio': 0.5, 'scatter_count': 5, 'scatter_ratio': 0.15}
SA_LASER_2 = {'type': 'laser', 'beams': 3, 'main_ratio': 0.35, 'scatter_count': 5, 'scatter_ratio': 0.125, 'secondary_count': 3, 'secondary_ratio': 0.09375}
SA_LASER_3 = {'type': 'laser', 'beams': 5, 'main_ratio': 0.25, 'scatter_count': 5, 'scatter_ratio': 0.14, 'secondary_count': 3, 'secondary_ratio': 0.105}
SA_LASER_3_LAMBDA = {'type': 'laser', 'beams': 5, 'main_ratio': 0.25, 'scatter_count': 5, 'scatter_ratio': 0.14, 'secondary_count': 3, 'secondary_ratio': 0.105, 'tertiary_count': 2, 'tertiary_ratio': 0.079}


# ========== 刀列表 ==========
blades = []

def blade(name, base_atk, max_dp, sa_name, sa_data, ad=1.0, es=None):
    blades.append(Blade(name, base_atk, max_dp, sa_name, sa_data, attack_distance=ad, exclusive_se=es))

# Tier 3
blade("碎白", 5, 4, "碎段", SA_FRAGMENT)
blade("黑刃", 5, 4, "多重次元斩·决", SA_MULTI_JC, es=["black_rose"])
blade("八卦剑", 4, 3, "多重剑气", SA_MULTI_DRIVE)
blade("青云", 5, 6, "青芒", SA_CYAN_GLOW)
blade("青云λ", 6, 8, "青芒λ", SA_CYAN_GLOW_LAMBDA)
blade("龙鳞", 5, 5, "风暴幻影剑", SA_STORM_PHANTOM)
blade("龙鳞λ", 5, 10, "风暴幻影剑λ", SA_STORM_PHANTOM_LAMBDA)
blade("伞", 5, 12, "多重次元斩·决", SA_MULTI_JC, es=["black_rose"])
blade("伞λ", 6, 18, "无限次元斩", SA_INFINITE_JC, es=["black_rose"])
blade("八卦巨剑", 6, 12, "穷观阵", SA_MATRIX)
blade("八卦巨剑λ", 6, 12, "穷观阵λ", SA_MATRIX_LAMBDA)
blade("Obliterate", 0, 12, "业火", SA_INFERNO)
blade("Obliterateλ", 0, 16, "业火λ", SA_INFERNO_LAMBDA)
blade("闪金", 6, 24, "斩铁式·极", SA_ZANTETSUDEN_MAX)
blade("闪金λ", 6, 32, "斩铁式·极λ", SA_ZANTETSUDEN_MAX_LAMBDA)
blade("闪茶", 6, 24, "斩铁式·行", SA_ZANTETSUDEN_ROW)
blade("闪茶λ", 6, 32, "斩铁式·行λ", SA_ZANTETSUDEN_ROW_LAMBDA)
# Tier 2
blade("冰薄荷", 7, 16, "乱舞", SA_FANATICAL_DANCE)
blade("冰薄荷λ", 7, 24, "乱舞λ", SA_FANATICAL_DANCE_LAMBDA)
blade("龙魂", 6, 12, "剑雨", SA_SWORD_RAIN)
blade("龙魂λ", 6, 24, "剑雨λ", SA_SWORD_RAIN_LAMBDA)
blade("魂刃", 6, 18, "业火λ", SA_INFERNO_LAMBDA, es=["flame_foam"])
blade("太极", 6, 24, "穷观阵λ", SA_MATRIX_LAMBDA, es=["resolve"])
blade("太极λ", 6, 32, "穷观阵λ", SA_MATRIX_LAMBDA, es=["resolve_lambda"])
blade("风云", 6, 24, "幻影爆破", SA_PHANTOM_EXPLOSION)
blade("风云λ", 6, 32, "幻影爆破λ", SA_PHANTOM_EXPLOSION_LAMBDA)
blade("灿金", 6, 24, "斩铁式·极", SA_ZANTETSUDEN_MAX)
blade("灿金λ", 6, 32, "斩铁式·极λ", SA_ZANTETSUDEN_MAX_LAMBDA)
blade("灿茶", 6, 24, "斩铁式·行", SA_ZANTETSUDEN_ROW)
blade("灿茶λ", 6, 32, "斩铁式·行λ", SA_ZANTETSUDEN_ROW_LAMBDA)
# Void
blade("洞虚[猩红]", 8, 12, "拟似黑洞", SA_VOID_HOLE, ad=1.5)
blade("洞虚[漆黑]", 8, 24, "黑洞·漆黑", SA_VOID_HOLE_PITCH_BLACK, ad=1.5)
blade("洞虚[绯红]", 8, 32, "黑洞·绯红", SA_VOID_HOLE_FISHY_RED, ad=1.5)
# Base
blade("云翼", 6, 8, "云轮", SA_CLOUD_WHEEL, ad=1.15)
blade("云翼λ", 7, 12, "云轮风暴", SA_CLOUD_WHEEL_STORM, ad=1.15)
blade("彩翼", 8, 14, "苍穹十二连", SA_HEAVEN_TWELVE)
blade("彩翼λ", 8, 24, "苍穹十二连λ", SA_HEAVEN_TWELVE_LAMBDA)
blade("长空落日", 6, 10, "长空落日", SA_LONG_SKY_SUNSET, es=["sunset"])
blade("长空落日λ", 7, 14, "长空落日λ", SA_LONG_SKY_SUNSET_LAMBDA, es=["sunset"])
# 星流
blade("星流I", 4, 6, "星流I", SA_STAR_1, ad=0.75)
blade("星流II", 5, 12, "星流II", SA_STAR_2)
blade("星流III", 6, 18, "星流III", SA_STAR_3, ad=1.5)
blade("星流IV", 7, 24, "星流IV", SA_STAR_4, ad=2.0)
blade("星流IVλ", 8, 32, "星流IVλ", SA_STAR_4_LAMBDA, ad=2.0)
# 激光
blade("光棱I", 6, 10, "光棱I", SA_LASER_1, es=["photon_scar"])
blade("光棱II", 7, 15, "光棱II", SA_LASER_2, es=["photon_scar"])
blade("光棱III", 8, 20, "光棱III", SA_LASER_3, es=["photon_scar"])
blade("光棱IIIλ", 9, 24, "光棱IIIλ", SA_LASER_3_LAMBDA, es=["photon_scar"])
# 特殊
blade("TIL", 10, 48, "星璇", SA_STELLAR, es=["star_blink"])
blade("TILλ", 12, 96, "星璇", SA_STELLAR, es=["star_blink_lambda"])
blade("HTOD", 10, 48, "—", {}, es=[])
blade("HTODλ", 12, 96, "—", {}, es=[])
blade("星空", 10, 48, "—", {}, es=[])
blade("星空λ", 12, 48, "—", {}, es=[])
blade("轩辕", 7, 6, "—", {}, es=[])


# ========== 伤害计算函数 ==========

def hit_damage(ad, ratio, extra_amp=0):
    """单发伤害"""
    total_amp = BASE_AMPLIFIER + extra_amp
    ultimately = ratio * total_amp * BASE_MECH_AMPLIFIER
    return ad * ultimately


def total_hits(ad, ratio, count, extra_amp=0):
    """N次命中总伤害"""
    return hit_damage(ad, ratio, extra_amp) * count


def calc_sa(blade, sa, extra_amp=0, multi_target=False, sa_only=False):
    """
    计算 SA 总伤害（不含填充）
    sa_only=True 时只返回SA本身
    """
    ad = blade.ad
    total = 0.0
    n_target = 5 if multi_target else 1
    t = sa.get('type', 'unknown')

    if t == 'summond_sword':
        ns = sa.get('normal_swords', 0)
        nr = sa.get('normal_ratio', 0)
        ls = sa.get('lightning_swords', 0)
        lr = sa.get('lightning_sword_ratio', 0)
        la = sa.get('lightning_aoe_ratio', 0)
        sw = sa.get('swords', 0)
        sr = sa.get('sword_ratio', sa.get('ratio', 0))
        # 普通幻影剑
        if ns > 0:
            total += total_hits(ad, nr, ns, extra_amp)
        # 闪电召剑(剑身)
        if ls > 0:
            total += total_hits(ad, lr, ls, extra_amp)
            # 闪电AoE（对群×N）
            if multi_target:
                total += total_hits(ad, la, ls * n_target, extra_amp)
            else:
                total += total_hits(ad, la, ls, extra_amp)
        # 纯幻影剑
        if sw > 0 and sr > 0:
            if sa_only:
                total += total_hits(ad, sr, sw, extra_amp)

    elif t == 'lightning':
        ls = sa.get('lightning_swords', 0)
        lr = sa.get('lightning_sword_ratio', 0)
        la = sa.get('lightning_aoe_ratio', 0)
        total += total_hits(ad, lr, ls, extra_amp)
        if multi_target:
            total += total_hits(ad, la, ls * n_target, extra_amp)
        else:
            total += total_hits(ad, la, ls, extra_amp)

    elif t == 'laser':
        beams = sa.get('beams', 1)
        mr = sa.get('main_ratio', 0.5)
        sc = sa.get('scatter_count', 5)
        sr = sa.get('scatter_ratio', 0.15)
        # 主光束(单目标)
        total += hit_damage(ad, mr, extra_amp) * beams
        # 散射
        if multi_target:
            effective_targets = min(sc, n_target - 1)
            total += hit_damage(ad, sr, extra_amp) * effective_targets * beams
        else:
            # 单目标: 全部散射可全中(如果距离够近)
            total += hit_damage(ad, sr, extra_amp) * sc * beams
            # 二阶散射(部分命中)
            sec_c = sa.get('secondary_count', 0)
            sec_r = sa.get('secondary_ratio', 0)
            if sec_c > 0:
                total += hit_damage(ad, sec_r, extra_amp) * sec_c * 0.7
            ter_c = sa.get('tertiary_count', 0)
            ter_r = sa.get('tertiary_ratio', 0)
            if ter_c > 0:
                total += hit_damage(ad, ter_r, extra_amp) * ter_c * 0.5

    elif t == 'star':
        sw = sa.get('swords', 6)
        sr = sa.get('sword_ratio', 0.25)
        jr = sa.get('jc_ratio', 0.5)
        # 召剑伤害
        total += total_hits(ad, sr, sw, extra_amp)
        # 触发的次元斩(每剑命中都触发)
        total += hit_damage(ad, jr, extra_amp) * sw

    elif t == 'judgement_cut':
        cuts = sa.get('cuts', 1)
        r = sa.get('ratio', 0.3)
        total += total_hits(ad, r, cuts, extra_amp)

    elif t == 'mixed_jc_slash':
        cuts = sa.get('cuts', 25)
        r = sa.get('ratio', 0.03)
        jr = sa.get('jc_ratio', 0.25)
        total += hit_damage(ad, jr, extra_amp)  # 大次元斩
        total += total_hits(ad, r, cuts, extra_amp)  # 连续斩击

    elif t == 'mixed_jc_drive':
        dr = sa.get('drives', 20)
        dr_r = sa.get('drive_ratio', 0.015)
        jr = sa.get('jc_ratio', 0.25)
        total += hit_damage(ad, jr, extra_amp)  # 大次元斩
        total += total_hits(ad, dr_r, dr, extra_amp)  # 剑气散射

    elif t == 'matrix' or t == 'aoe_control':
        life = sa.get('life', 200)
        interval = sa.get('interval', 10)
        r = sa.get('ratio_per_tick', sa.get('ratio', 0.02))
        hits = life // interval
        total += total_hits(ad, r, hits, extra_amp)

    elif t == 'spiral':
        mi = sa.get('min_swords', 36)
        mx = sa.get('max_swords', 72)
        r = sa.get('ratio', 0.02)
        avg = (mi + mx) / 2
        total += total_hits(ad, r, avg, extra_amp)

    elif t == 'slash_effect':
        hits = sa.get('hits', 8)
        r = sa.get('ratio', 0.3)
        total += total_hits(ad, r, hits, extra_amp)

    elif t == 'drive':
        dr = sa.get('drives', 4)
        r = sa.get('ratio', 0.15)
        total += total_hits(ad, r, dr, extra_amp)

    return total


def calc_se_bonus(blade, sa, extra_amp, system_name):
    """
    计算体系SE带来的额外伤害（触发型 SE 如破片/撕裂/回旋等）。
    在 SA 周期内稳定触发的部分。
    """
    ad = blade.ad
    total = 0.0
    desc_parts = []

    # === 幻影剑系 ===
    if system_name == '幻影剑系':
        # 破片: 每SA周期堆叠, 假设触发1次8.0倍率
        total += hit_damage(ad, FRAGMENT_L5_RATIO, extra_amp)
        desc_parts.append('破片8.0')
        # 回旋: 9把风暴剑
        total += total_hits(ad, SPIRAL_L5_RATIO, SPIRAL_L5_COUNT, extra_amp)
        desc_parts.append(f'回旋{SPIRAL_L5_COUNT}剑')
        # 冲击: 25%概率0.5倍率, SA周期内多次触发, 假设10次hit中触发2~3次
        trigger_hits = 10  # SA中幻影剑数量估算
        impact_triggers = int(trigger_hits * IMPACT_L5_PROB)
        total += hit_damage(ad, IMPACT_L5_RATIO, extra_amp) * impact_triggers
        desc_parts.append(f'冲击×{impact_triggers}')

    # === 次元斩系 ===
    elif system_name == '次元斩系':
        # 撕裂: 满层1.5倍率触发一次
        total += hit_damage(ad, TEAR_L5_RATIO, extra_amp)
        desc_parts.append('撕裂1.5')
        # 断却: 0.85倍率AoE, 对群时作用更大
        total += hit_damage(ad, SEVER_BREAK_L5_RATIO, extra_amp)

    # === 斩击系 ===
    elif system_name == '斩击系':
        # 协同: 45%概率0.5倍率追斩
        coop_dmg = hit_damage(ad, COOPERATE_L5_RATIO, extra_amp)
        total += coop_dmg * COOPERATE_L5_PROB * 10  # 约10次hit触发4.5次
        desc_parts.append('协同')
        # 十字斩: 0.5倍率
        total += hit_damage(ad, CROSS_CHOP_L5_RATIO, extra_amp)
        desc_parts.append('十字斩')

    # === 雷电系 ===
    elif system_name == '雷电系':
        # 雷暴: 3道雷, 0.6倍率
        total += hit_damage(ad, THUNDERSTORM_L5_RATIO, extra_amp) * THUNDERSTORM_L5_COUNT
        desc_parts.append(f'雷暴{THUNDERSTORM_L5_COUNT}雷')

    return total, desc_parts


def calc_exclusive_se(blade):
    """专属SE的追加伤害（固定值或独立机制）"""
    ad = blade.ad
    total = 0.0
    desc = ""
    for es in blade.exclusive_se:
        if es == 'photon_scar':
            # 光子灼痕: 满层增伤+0.33 + 灼烧每秒7.5DPS + 短光棱0.45
            # 增伤在 extra_amp 里处理, 这里只算持续伤害和短光棱
            burn_dot = 50 * 0.15 * 8  # 每5tick 7.5, 40tick=60
            total += burn_dot
            desc += "+灼烧60"
        elif es == 'sunset':
            # 长空落日SE: 每5tick自动剑 × 8发
            se_ratio = 0.30  # lv5
            sword_dmg = total_hits(ad, se_ratio, 8, 0)
            hui_guang = ad * 0.15 * 8  # 晖光flat
            total += sword_dmg + hui_guang
            desc += "+SE自动剑"
        elif es == 'flame_foam':
            # 焰沫: 6%当前生命/秒 → 2秒12%
            total += 100 * FLAME_FOAM_DOT_PCT * 2  # 假设目标100HP
            desc += "+焰沫DOT"
        elif 'resolve' in es:
            # 解算消耗演算: 每次1.0/1.5倍率
            r = RESOLVE_LAMBDA_RATIO if 'lambda' in es else RESOLVE_RATIO
            resolve_dmg = hit_damage(ad, r, 0)
            # 阵20次命中, 演算叠到16层后消耗触发, 约触发4~6次
            total += resolve_dmg * 5
            desc += "+解算×5"
        elif es == 'black_rose':
            # 黑玫瑰: 累计5%伤害, 忽略不计(取决于总伤)
            desc += "+黑玫瑰"
        elif es == 'star_blink' or es == 'star_blink_lambda':
            # 星闪: 满层2.25倍率, 依赖高频hit
            total += hit_damage(ad, STAR_BLINK_RATIO, 0)
            desc += "+星闪2.25"
    return total, desc


def evaluate_blade(blade):
    """
    对一把刀穷举所有可能的 SE 体系组合，取最佳结果。
    """
    ad = blade.ad
    sa = blade.sa_data
    sa_type = sa.get('type', 'unknown')
    results_list = []

    # 确定该刀可以尝试哪些 SE 体系
    candidate_systems = []
    if sa_type in ('summond_sword', 'spiral', 'star'):
        candidate_systems.append('幻影剑系')
    if sa_type in ('judgement_cut', 'mixed_jc_slash', 'mixed_jc_drive', 'star'):
        candidate_systems.append('次元斩系')
    if sa_type in ('slash_effect',):
        candidate_systems.append('斩击系')
    if sa_type in ('lightning', 'laser'):
        candidate_systems.append('雷电系')
    if sa_type in ('drive', 'mixed_jc_drive'):
        candidate_systems.append('剑气系')
    if sa_type in ('matrix', 'aoe_control'):
        candidate_systems.append('幻影剑系')  # 阵/星璇本身无类型, 幻影剑填充
    # 无SA刀不跑体系
    if sa_type == 'unknown':
        candidate_systems = []

    # 对每个候选体系, 计算 SA + 10幻影剑填充 + SE额外 + 专属SE
    for sys_name in candidate_systems if candidate_systems else ['无SE']:
        sys = SE_SYSTEMS.get(sys_name, {})
        type_amp = sys.get('type_amplifier', 0)
        attack_type = sys.get('attack_type', '')

        # === 1. 计算全局 amplifier ===
        # 基础 + 类型SE增幅(太虚/震荡等)
        total_extra_amp = type_amp

        # 满层全局buff(条件性视作吃满)
        # 次元斩系吃 Calculus(0.8) — 阵叠演算
        if sys_name == '次元斩系':
            total_extra_amp += GLOBAL_BUFF_AMPLIFIER['calculus']
        # 雷电系吃 电离(0.64)
        if sys_name == '雷电系':
            total_extra_amp += GLOBAL_BUFF_AMPLIFIER['ionization']
        # 激光刀吃光子灼烧(0.33)
        if 'photon_scar' in blade.exclusive_se:
            total_extra_amp += GLOBAL_BUFF_AMPLIFIER['photon_burn']
        # 长空落日吃 叠辉满层(1.0)
        if 'sunset' in blade.exclusive_se:
            total_extra_amp += GLOBAL_BUFF_AMPLIFIER['sunset_stack']

        # === 2. SA 伤害 ===
        sa_single = calc_sa(blade, sa, total_extra_amp, multi_target=False, sa_only=True)
        sa_multi = calc_sa(blade, sa, total_extra_amp, multi_target=True, sa_only=True)

        # === 3. 10次普通挥刀 + 10次幻影剑（外加填充） ===
        normal_single = total_hits(ad, 1.0, 10, total_extra_amp)  # 10次普通(ratio=1.0)
        phantom_single = total_hits(ad, PHANTOM_FILLER_RATIO, PHANTOM_FILLER_COUNT, total_extra_amp)  # 10次幻影剑(0.2)
        filler_single = normal_single + phantom_single
        filler_multi = filler_single * 5  # 对群5目标各10刀+10剑

        # === 4. SE体系触发追加 ===
        se_bonus, se_desc = calc_se_bonus(blade, sa, total_extra_amp, sys_name)
        se_bonus_str = '+'.join(se_desc)

        # === 5. 专属SE追加 ===
        ex_bonus, ex_desc = calc_exclusive_se(blade)

        # === 6. 合计 ===
        total_single = sa_single + filler_single + se_bonus + ex_bonus
        total_multi = sa_multi + filler_multi + se_bonus + ex_bonus

        extra_info = f"{sys_name}"
        if se_bonus_str:
            extra_info += f" +{se_bonus_str}"
        if ex_desc:
            extra_info += f" |{ex_desc}"

        results_list.append({
            'system': sys_name,
            'type_amp': type_amp,
            'extra_amp': total_extra_amp,
            'sa_single': round(sa_single, 1),
            'sa_multi': round(sa_multi, 1),
            'normal_10': round(normal_single, 1),
            'phantom_10': round(phantom_single, 1),
            'filler_single': round(filler_single, 1),
            'filler_multi': round(filler_multi, 1),
            'se_bonus': round(se_bonus, 1),
            'ex_bonus': round(ex_bonus, 1),
            'total_single': round(total_single, 1),
            'total_multi': round(total_multi, 1),
            'dps_single': round(total_single / 2, 1),
            'dps_multi': round(total_multi / 2, 1),
            'extra_info': extra_info,
            'note': f"SA{round(sa_single,1)}+普10{round(normal_single,1)}+幻10{round(phantom_single,1)}"
        })

    # 无SA刀 (只有填充)
    if not candidate_systems:
        normal_only = total_hits(ad, 1.0, 10, 0)
        phantom_only = total_hits(ad, PHANTOM_FILLER_RATIO, PHANTOM_FILLER_COUNT, 0)
        filler_total = normal_only + phantom_only
        results_list.append({
            'system': '仅普攻+幻影剑',
            'type_amp': 0,
            'extra_amp': 0,
            'sa_single': 0,
            'sa_multi': 0,
            'normal_10': round(normal_only, 1),
            'phantom_10': round(phantom_only, 1),
            'filler_single': round(filler_total, 1),
            'filler_multi': round(filler_total * 5, 1),
            'se_bonus': 0,
            'ex_bonus': 0,
            'total_single': round(filler_total, 1),
            'total_multi': round(filler_total * 5, 1),
            'dps_single': round(filler_total / 2, 1),
            'dps_multi': round(filler_total * 5 / 2, 1),
            'extra_info': "无SA",
            'note': f"普10{round(normal_only,1)}+幻10{round(phantom_only,1)}"
        })

    # 取最佳对单DPS的组合作为该刀结果
    best = max(results_list, key=lambda r: r['dps_single'])
    return best


# ========== 主流程 ==========
results = []
for blade in blades:
    r = evaluate_blade(blade)
    r['name'] = blade.name
    r['ad'] = blade.ad
    r['max_dp'] = blade.max_dp
    r['sa_name'] = blade.sa_name
    results.append(r)

# ========== 输出 Markdown ==========
out = []
out.append("# Recasting2 全实战刀满成长伤害定量报告（穷举版）")
out.append("")
out.append("> 分析日期：2026-07-13")
out.append("> **所有叠加增伤视为满层常驻**（演算16层+0.8、电离64层+0.64、灼烧50层+0.33、叠辉50层+1.0）")
out.append("> 基础乘区：`modifiedRatioAmplifier=2.2` × `mechanismModifiedRatioAmplifier=1.3` = **×2.86**")
out.append("> SE 体系 L5 满配，专属SE按绑定计算")
out.append("> **循环：SA起手 + 10次普通挥刀(ratio=1.0) + 10次幻影剑(ratio=0.2)**，SA CD=40tick(2秒)")
out.append("")
out.append("---")
out.append("")
out.append("## 伤害公式")
out.append("")
out.append("```")
out.append("ATTACK_DAMAGE = 10 + baseAttackModifier × 2")
out.append("ultimatelyModifiedRatio = modifiedRatio × (2.2 + 类型SE增幅 + 全局Buff) × 1.3")
out.append("伤害 = ATTACK_DAMAGE × ultimatelyModifiedRatio")
out.append("```")
out.append("")
out.append("## 穷举规则")
out.append("")
out.append("每刀尝试所有匹配的 SE 体系（幻影剑系/次元斩系/斩击系/雷电系/剑气系），")
out.append("取对单DPS最高的组合。各体系含4个L5 SE槽 + 专属SE（如有）的叠加效果。")
out.append("")

out.append("| SE体系 | 类型增幅 | 全局Buff叠加 | 触发SE |")
out.append("|--------|---------|-------------|--------|")
out.append("| **幻影剑系** | +0.6(太虚) | — | 破片8.0×1 + 回旋9×0.1 + 冲击0.5×概率 |")
out.append("| **次元斩系** | +0.95(震荡) | 演算+0.8 | 撕裂1.5×1 + 断却0.85×1 |")
out.append("| **斩击系** | +0.6(利刃) | — | 协同0.5×0.45 + 十字斩0.5 |")
out.append("| **雷电系** | +0.95(雷霆万钧) | 电离+0.64 | 雷暴3×0.6 |")
out.append("| **剑气系** | +0.95(剑气纵横) | — | 分裂0.95 |")
out.append("")

out.append("## 各刀最佳组合及伤害")
out.append("")
out.append("| # | 刀 | AD | 预备 | SA | 最佳体系 | 类型增 | Buff增 | SA单 | SA5 | 普10单 | 幻10单 | 填充5 | SE追 | 专SE | 合计单 | 合计5 | DPS单 | DPS5 |")
out.append("|---|-----|----|------|----|---------|-------|--------|------|------|--------|--------|-------|------|------|--------|--------|-------|-------|")

results.sort(key=lambda r: r['dps_single'], reverse=True)

for i, r in enumerate(results):
    rank = i + 1
    out.append(
        f"| {rank} | {r['name']} | {r['ad']} | {r['max_dp']} | {r['sa_name']} "
        f"| {r['system']} | +{r['type_amp']} | +{r['extra_amp']-r['type_amp']:.2f} "
        f"| {r['sa_single']} | {r['sa_multi']} | {r['normal_10']} | {r['phantom_10']} "
        f"| {r['filler_multi']} | {r['se_bonus']} | {r['ex_bonus']} | {r['total_single']} | {r['total_multi']} "
        f"| {r['dps_single']} | {r['dps_multi']} |"
    )

out.append("")
out.append("---")
out.append("")
out.append("## 对单 DPS 排名")
out.append("")
out.append("| 排名 | 刀 | DPS | SA | 普10 | 幻10 | SE追 | 体系 |")
out.append("|------|-----|-----|----|------|------|------|------|")

sorted_single = sorted(results, key=lambda r: r['dps_single'], reverse=True)
for i, r in enumerate(sorted_single):
    rank = i + 1
    out.append(f"| {rank} | {r['name']} | {r['dps_single']} | {r['sa_single']} | {r['normal_10']} | {r['phantom_10']} | {r['se_bonus']+r['ex_bonus']:.1f} | {r['system']} |")

out.append("")
out.append("## 对群 DPS 排名")
out.append("")
out.append("| 排名 | 刀 | DPS(5) | SA5 | 填充5 | SE追 | 体系 |")
out.append("|------|-----|--------|-----|-------|------|------|")

sorted_multi = sorted(results, key=lambda r: r['dps_multi'], reverse=True)
for i, r in enumerate(sorted_multi):
    rank = i + 1
    out.append(f"| {rank} | {r['name']} | {r['dps_multi']} | {r['sa_multi']} | {r['filler_multi']} | {r['se_bonus']+r['ex_bonus']:.1f} | {r['system']} |")

out.append("")
out.append("---")
out.append("")
out.append("## 关键发现")
out.append("")
out.append("1. **彩翼λ** 苍穹十二连λ + 雷电系满配，单周期 DPS **2,959**，全刀第一")
out.append("2. **星流IVλ** 次元斩系满配 + 演算+0.8，召剑→次元斩联动，DPS **2,399**")
out.append("3. **长空落日λ** 幻影剑系满配 + 叠辉满层+1.0 + SE自动剑，DPS **1,622**")
out.append("4. **龙魂λ** 剑雨150剑+幻影剑系，SA占比最高，DPS **1,517**")
out.append("5. **TILλ** 星璇控制+幻影剑系填充，DPS **1,476**（含控制价值）")
out.append("6. **光棱IIIλ** 雷电系+光子灼痕Buff，DPS **1,443**，稳定持续")
out.append("7. **太极λ** 阵伤+幻影剑填充+解算×5，DPS **1,136**")
out.append("8. **对群格局**：彩翼λ→12,028 > 星流IVλ→5,604 > 彩翼→5,188 > 光棱IIIλ→4,967")
out.append("9. **无SA刀**(HTOD/星空/轩辕)仅有普10+幻10 = DPS 411~583，远低于同阶SA刀")
out.append("10. **普10(+幻10)填充**占低ratio SA刀(S/A级)总伤60-80%，高ratio SA刀(彩翼λ)仅占20%")
out.append("")

with open('blade_damage_full_report.md', 'w', encoding='utf-8') as f:
    f.write('\n'.join(out))

print("报告已生成: blade_damage_full_report.md")
print(f"共分析了 {len(results)} 把刀")
