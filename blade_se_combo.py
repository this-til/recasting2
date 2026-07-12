#!/usr/bin/env python3
"""
穷尽所有个体SE组合（每把刀C(N,4)枚举），输出各刀 Top 1~5
循环：SA起手 + 10普攻(ratio=1.0) + 10幻影剑(ratio=0.2)
"""
import itertools

# ─── 基础公式 ───
# 最终伤害 = ATTACK_DAMAGE × modifiedRatio × totalAmplifier × 1.3
# totalAmplifier = 2.2(基础) + SE类型增幅(累加) + 全局Buff(累加)
BASE_AMP = 2.2
MECH_AMP = 1.3

def hit(ad, ratio, extra_amp=0):
    """extra_amp = 超出BASE_AMP的部分（SE+全局Buff贡献）"""
    return ad * ratio * (BASE_AMP + extra_amp) * MECH_AMP

def nhits(ad, ratio, n, extra_amp=0):
    return hit(ad, ratio, extra_amp) * n

# ─── 个体SE池(L5满级) ───
# (名称, 类别, 匹配at, 参数)
# 类别: amp→加幅, trig→触发, buff→全局
# 匹配at: 攻击类型名 / any / sa
SE = [
    # === 类型增幅 ===
    ("太虚",    "amp", "summond_sword",  {"v":0.60}),
    ("利刃",    "amp", "slash_effect",   {"v":0.60}),
    ("震荡",    "amp", "judgement_cut",  {"v":0.95}),
    ("剑气纵横","amp", "drive",          {"v":0.95}),
    ("雷霆万钧","amp", "lightning",      {"v":0.95}),
    # === 通用触发 ===
    ("冲击",    "trig","any",  {"prob":0.25, "r":0.50, "d":"冲击0.5×25%"}),
    ("蓄能",    "trig","any",  {"hpi":48,    "r":0.60, "d":"蓄能0.6×1/48"}),
    # === 幻影剑触发 ===
    ("破片",    "trig","summond_sword", {"stk":12,"r":8.0, "d":"破片8.0"}),
    ("回旋",    "trig","summond_sword", {"stk":12,"n":9,"r":0.1, "d":"回旋9×0.1"}),
    # === 次元斩触发 ===
    ("撕裂",    "trig","judgement_cut", {"stk":12,"r":1.5, "d":"撕裂1.5"}),
    ("断却",    "trig","judgement_cut", {"r":0.85, "d":"断却0.85"}),
    ("断罪",    "trig","sa",            {"r":0.70, "d":"断罪0.7"}),
    ("风暴",    "trig","judgement_cut", {"n":6, "r":0.25, "d":"风暴6×0.25"}),
    # === 斩击触发 ===
    ("协同",    "trig","slash_effect",  {"prob":0.45, "r":0.5, "d":"协同0.5×45%"}),
    ("十字斩",  "trig","slash_effect",  {"r":0.50, "d":"十字斩0.5"}),
    # === 剑气/Drive触发 ===
    ("分裂",    "trig","drive",         {"r":0.95, "d":"分裂0.95"}),
    # === 雷电触发 ===
    ("电离",    "buff","lightning",     {"v":0.64, "d":"电离+0.64"}),
    ("雷暴",    "trig","sa",            {"cond":"lightning","n":3,"r":0.6,"d":"雷暴3×0.6"}),
]

# 专属SE(绑定特定刀)
EXSE = {
    "photon_scar":    {"d":"灼痕+0.33+灼烧60","bamp":0.33,"flat":60},
    "sunset":         {"d":"SE自动剑+晖光","fn":lambda ad: nhits(ad,0.30,8,0)+ad*0.15*8},
    "flame_foam":     {"d":"焰沫DOT12","flat":12},
    "resolve":        {"d":"解算×5","fn":lambda ad: hit(ad,1.0,0)*5},
    "resolve_lambda": {"d":"解算λ×5","fn":lambda ad: hit(ad,1.5,0)*5},
    "black_rose":     {"d":"黑玫瑰","flat":20},
    "star_blink":     {"d":"星闪2.25","fn":lambda ad: hit(ad,2.25,0)},
    "star_blink_lambda":{"d":"星闪λ","fn":lambda ad: hit(ad,2.25,0)},
}

GLOBAL_BUFF = {
    "calculus": 0.80,     # 演算16层
    "sunset_stack": 1.0,  # 叠辉50层
    "photon_burn": 0.33,  # 灼烧50层
    "ionization": 0.64,   # 电离64层
}

# ─── SA参数 ───
SA = {}
def sa(k, v):
    SA[k]=v

sa("clw",    {"t":"summond_sword","ns":6,"nr":0.2,"ls":10,"lr":0.2,"la":0.4})
sa("clw_s",  {"t":"summond_sword","ns":10,"nr":0.2,"ls":8,"lr":0.2,"la":0.4})
sa("h12",    {"t":"lightning","ls":12,"lr":0.1,"la":0.3})
sa("h12l",   {"t":"lightning","ls":18,"lr":0.5,"la":1.3})
sa("lss",    {"t":"summond_sword","sw":16,"sr":0.12})
sa("lssl",   {"t":"summond_sword","sw":32,"sr":0.12})
sa("sp12",   {"t":"summond_sword","sw":12,"sr":0.15})
sa("sp18",   {"t":"summond_sword","sw":18,"sr":0.20})
sa("sr150",  {"t":"summond_sword","sw":150,"sr":0.05})
sa("sr200",  {"t":"summond_sword","sw":200,"sr":0.08})
sa("pe36",   {"t":"spiral","mi":36,"mx":72,"r":0.02})
sa("pe48",   {"t":"spiral","mi":48,"mx":96,"r":0.03})
sa("jc4",    {"t":"judgement_cut","c":4,"r":0.3})
sa("jc12",   {"t":"judgement_cut","c":12,"r":0.5})
sa("inf",    {"t":"judgement_cut","c":1,"r":0.3})
sa("infl",   {"t":"judgement_cut","c":1,"r":0.4})
sa("vh",     {"t":"judgement_cut","life":20,"r":0.05})
sa("vhpb",   {"t":"judgement_cut","life":30,"r":0.08})
sa("vhfr",   {"t":"judgement_cut","life":40,"r":0.10})
sa("cg8",    {"t":"slash_effect","h":8,"r":0.3})
sa("cg12",   {"t":"slash_effect","h":12,"r":0.35})
sa("fd15",   {"t":"slash_effect","h":15,"r":0.4})
sa("fd20",   {"t":"slash_effect","h":20,"r":0.45})
sa("frag",   {"t":"slash_effect","h":1,"r":0.3})
sa("ztm25",  {"t":"mixed","c":25,"r":0.03,"jr":0.25})
sa("ztm35",  {"t":"mixed","c":35,"r":0.04,"jr":0.30})
sa("ztr20",  {"t":"mixed_d","dr":20,"drr":0.015,"jr":0.25})
sa("ztr30",  {"t":"mixed_d","dr":30,"drr":0.02,"jr":0.30})
sa("mat",    {"t":"matrix","life":200,"int":10,"r":0.02})
sa("matl",   {"t":"matrix","life":200,"int":10,"r":0.03})
sa("ste",    {"t":"aoe","tick":60,"rpt":0.01})
sa("star1",  {"t":"star","sw":6,"sr":0.25,"jr":0.5})
sa("star2",  {"t":"star","sw":8,"sr":0.30,"jr":0.6})
sa("star3",  {"t":"star","sw":10,"sr":0.35,"jr":0.7})
sa("star4",  {"t":"star","sw":12,"sr":0.40,"jr":0.8})
sa("star4l", {"t":"star","sw":16,"sr":0.45,"jr":0.9})
sa("las1",   {"t":"laser","b":1,"mr":0.5,"sc":5,"sr":0.15})
sa("las2",   {"t":"laser","b":3,"mr":0.35,"sc":5,"sr":0.125,"sec":3,"ser":0.094})
sa("las3",   {"t":"laser","b":5,"mr":0.25,"sc":5,"sr":0.14,"sec":3,"ser":0.105})
sa("las3l",  {"t":"laser","b":5,"mr":0.25,"sc":5,"sr":0.14,"sec":3,"ser":0.105,"tec":2,"ter":0.079})
sa("md4",    {"t":"drive","dr":4,"r":0.15})

# ─── 刀定义 ───
class Blade:
    def __init__(s,nm,ba,dp,sa_n,sa_k,ad=1.0,ex=None):
        s.name=nm;s.ba=ba;s.dp=dp;s.sa_n=sa_n;s.sa=SA.get(sa_k,{});s.ad=10+ba*2
        s.adist=ad;s.ex=ex

BLADES = []
def B(nm,ba,dp,sa_n,sa_k,ad=1.0,ex=None):
    BLADES.append(Blade(nm,ba,dp,sa_n,sa_k,ad,ex))

B("碎白",5,4,"碎段","frag")
B("黑刃",5,4,"多重JC","jc4",ex="black_rose")
B("八卦剑",4,3,"多重剑气","md4")
B("青云",5,6,"青芒","cg8")
B("青云λ",6,8,"青芒λ","cg12")
B("龙鳞",5,5,"风暴幻影剑","sp12")
B("龙鳞λ",5,10,"风暴幻影剑λ","sp18")
B("伞",5,12,"多重JC","jc4",ex="black_rose")
B("伞λ",6,18,"无限次元斩","jc12",ex="black_rose")
B("八卦巨剑",6,12,"穷观阵","mat")
B("八卦巨剑λ",6,12,"穷观阵λ","matl")
B("Obliterate",0,12,"业火","inf")
B("Obliterateλ",0,16,"业火λ","infl")
B("闪金",6,24,"斩铁式·极","ztm25")
B("闪金λ",6,32,"斩铁式·极λ","ztm35")
B("闪茶",6,24,"斩铁式·行","ztr20")
B("闪茶λ",6,32,"斩铁式·行λ","ztr30")
B("冰薄荷",7,16,"乱舞","fd15")
B("冰薄荷λ",7,24,"乱舞λ","fd20")
B("龙魂",6,12,"剑雨","sr150")
B("龙魂λ",6,24,"剑雨λ","sr200")
B("魂刃",6,18,"业火λ","infl",ex="flame_foam")
B("太极",6,24,"穷观阵λ","matl",ex="resolve")
B("太极λ",6,32,"穷观阵λ","matl",ex="resolve_lambda")
B("风云",6,24,"幻影爆破","pe36")
B("风云λ",6,32,"幻影爆破λ","pe48")
B("灿金",6,24,"斩铁式·极","ztm25")
B("灿金λ",6,32,"斩铁式·极λ","ztm35")
B("灿茶",6,24,"斩铁式·行","ztr20")
B("灿茶λ",6,32,"斩铁式·行λ","ztr30")
B("洞虚[猩红]",8,12,"拟似黑洞","vh",ad=1.5)
B("洞虚[漆黑]",8,24,"黑洞·漆黑","vhpb",ad=1.5)
B("洞虚[绯红]",8,32,"黑洞·绯红","vhfr",ad=1.5)
B("云翼",6,8,"云轮","clw",ad=1.15)
B("云翼λ",7,12,"云轮风暴","clw_s",ad=1.15)
B("彩翼",8,14,"苍穹十二连","h12")
B("彩翼λ",8,24,"苍穹十二连λ","h12l")
B("长空落日",6,10,"长空落日","lss",ex="sunset")
B("长空落日λ",7,14,"长空落日λ","lssl",ex="sunset")
B("星流I",4,6,"星流I","star1",ad=0.75)
B("星流II",5,12,"星流II","star2")
B("星流III",6,18,"星流III","star3",ad=1.5)
B("星流IV",7,24,"星流IV","star4",ad=2.0)
B("星流IVλ",8,32,"星流IVλ","star4l",ad=2.0)
B("光棱I",6,10,"光棱I","las1",ex="photon_scar")
B("光棱II",7,15,"光棱II","las2",ex="photon_scar")
B("光棱III",8,20,"光棱III","las3",ex="photon_scar",ad=1.2)
B("光棱IIIλ",9,24,"光棱IIIλ","las3l",ex="photon_scar",ad=1.2)
B("TIL",10,48,"星璇","ste",ex="star_blink")
B("TILλ",12,96,"星璇","ste",ex="star_blink_lambda")
B("HTOD",10,48,"—","__none__",ex=None)
B("HTODλ",12,96,"—","__none__",ex=None)
B("星空",10,48,"—","__none__",ex=None)
B("星空λ",12,48,"—","__none__",ex=None)
B("轩辕",7,6,"—","__none__",ex=None)


# ─── 复合SA类型 → 子类型映射（用于amp匹配） ───
AMP_SUB = {
    'star':    {'summond_sword', 'judgement_cut'},
    'mixed':   {'judgement_cut', 'slash_effect'},
    'mixed_d': {'judgement_cut', 'drive'},
    'lightning': {'summond_sword', 'lightning'},  # 闪电召剑含剑身+闪电
}
def match_at(st, at):
    """检查SE的攻击类型at是否匹配SA类型st"""
    if at == st: return True
    if st in AMP_SUB and at in AMP_SUB[st]: return True
    return False
def sa_dmg(b, eamp, multi=False):
    """返回(总伤, hit统计dict)"""
    ad=b.ad; sa=b.sa; nt=sa.get('t','?')
    if not sa or nt=='?': return 0,{}
    n5=5 if multi else 1
    tot=0.0
    h={k:0 for k in ('ss','jc','se','lt','dr','tot')}

    if nt=='summond_sword':
        ns=sa.get('ns',0);nr=sa.get('nr',0)
        ls=sa.get('ls',0);lr=sa.get('lr',0);la=sa.get('la',0)
        sw=sa.get('sw',0);sr=sa.get('sr',0)
        if ns: tot+=nhits(ad,nr,ns,eamp); h['ss']+=ns
        if ls:
            tot+=nhits(ad,lr,ls,eamp); h['ss']+=ls; h['lt']+=ls
            tot+=nhits(ad,la,ls*(n5 if multi else 1),eamp)
        if sw and sr: tot+=nhits(ad,sr,sw,eamp); h['ss']+=sw

    elif nt=='lightning':
        ls=sa.get('ls',0);lr=sa.get('lr',0);la=sa.get('la',0)
        tot+=nhits(ad,lr,ls,eamp); h['ss']+=ls; h['lt']+=ls
        tot+=nhits(ad,la,ls*(n5 if multi else 1),eamp)

    elif nt=='laser':
        b=sa.get('b',1);mr=sa.get('mr',0.5);sc=sa.get('sc',5);sv=sa.get('sr',0.15)
        tot+=hit(ad,mr,eamp)*b; h['lt']+=b
        if multi: tot+=hit(ad,sv,eamp)*min(sc,n5-1)*b
        else:
            tot+=hit(ad,sv,eamp)*sc*b; h['lt']+=b*sc
            sec=sa.get('sec',0);ser=sa.get('ser',0)
            if sec: tot+=hit(ad,ser,eamp)*sec*0.7; h['lt']+=int(sec*0.7)
            tec=sa.get('tec',0);ter=sa.get('ter',0)
            if tec: tot+=hit(ad,ter,eamp)*tec*0.5

    elif nt=='star':
        sw=sa.get('sw',6);sr=sa.get('sr',0.25);jr=sa.get('jr',0.5)
        tot+=nhits(ad,sr,sw,eamp); h['ss']+=sw
        tot+=hit(ad,jr,eamp)*sw; h['jc']+=sw

    elif nt=='judgement_cut':
        c=sa.get('c',1);r=sa.get('r',0.3)
        tot+=nhits(ad,r,c,eamp); h['jc']+=c

    elif nt=='mixed':
        c=sa.get('c',25);r=sa.get('r',0.03);jr=sa.get('jr',0.25)
        tot+=hit(ad,jr,eamp); h['jc']+=1
        tot+=nhits(ad,r,c,eamp); h['se']+=c

    elif nt=='mixed_d':
        dr=sa.get('dr',20);drr=sa.get('drr',0.015);jr=sa.get('jr',0.25)
        tot+=hit(ad,jr,eamp); h['jc']+=1
        tot+=nhits(ad,drr,dr,eamp); h['dr']+=dr

    elif nt in ('matrix','aoe'):
        life=sa.get('life',sa.get('tick',200));iv=sa.get('int',10)
        r=sa.get('rpt',sa.get('r',0.02));hc=life//iv
        tot+=nhits(ad,r,hc,eamp)

    elif nt=='spiral':
        mi=sa.get('mi',36);mx=sa.get('mx',72);r=sa.get('r',0.02)
        tot+=nhits(ad,r,(mi+mx)/2,eamp)

    elif nt=='slash_effect':
        hc=sa.get('h',8);r=sa.get('r',0.3)
        tot+=nhits(ad,r,hc,eamp); h['se']+=hc

    elif nt=='drive':
        dr=sa.get('dr',4);r=sa.get('r',0.15)
        tot+=nhits(ad,r,dr,eamp); h['dr']+=dr

    h['tot']=sum(h.values())
    return tot,h

# ─── 评估单个SE组合 ───
def eval_combo(b, se_list):
    ad=b.ad; sa=b.sa; st=sa.get('t','?')
    eamp=0.0  # 额外amplifier(超出BASE_AMP)

    # 1. 累加类型amp
    for sn in se_list:
        s=next(x for x in SE if x[0]==sn)
        if s[1]=='amp' and match_at(st, s[2]): eamp+=s[3]['v']
        if s[1]=='buff' and match_at(st, s[2]): eamp+=s[3]['v']

    # 2. 专属SE的buff amp
    if b.ex and b.ex in EXSE:
        eamp+=EXSE[b.ex].get('bamp',0)

    # 3. 全局Buff
    if b.ex=='photon_scar': eamp+=GLOBAL_BUFF['photon_burn']
    if b.ex=='sunset':      eamp+=GLOBAL_BUFF['sunset_stack']
    # 如果装了震荡且SA是次元斩系→吃演算
    if st in ('judgement_cut','mixed','mixed_d','star') and \
       any(s[0]=='震荡' and s[1]=='amp' for s in SE if s[0] in se_list):
        eamp+=GLOBAL_BUFF['calculus']

    # SA伤害
    sas, hc = sa_dmg(b, eamp, False)
    sam, _  = sa_dmg(b, eamp, True)

    # 填充
    n10 = nhits(ad, 1.0, 10, eamp)
    p10 = nhits(ad, 0.2, 10, eamp)

    # hit统计(用于触发SE)
    nss = hc.get('ss',0)+10  # SA幻影剑 + 10幻影剑
    njc = hc.get('jc',0)
    nse = hc.get('se',0)+10  # SA斩击 + 10普攻
    nlt = hc.get('lt',0)
    ndr = hc.get('dr',0)
    ntot = hc.get('tot',0)+20

    # 触发SE
    trig_dmg=0.0; parts=[]
    for sn in se_list:
        s=next(x for x in SE if x[0]==sn)
        if s[1]!='trig': continue
        p=s[3]; d_=p.get('d',sn)
        
        # 判断是否触发
        should_trigger = False
        if s[2]=='any':
            should_trigger = True
        elif s[2]=='sa' and hc.get('tot',0) > 0:
            should_trigger = True
        elif match_at(st, s[2]):
            should_trigger = True
        
        if not should_trigger:
            continue
        if 'cond' in p and p['cond']=='lightning' and nlt==0:
            continue
        
        # 计算触发伤害
        if 'prob' in p and 'hpi' not in p:
            # 概率触发: 冲击/协同
            if match_at(st, 'slash_effect') and s[2] in ('slash_effect',):
                n = int(nse * p['prob'])
            else:
                n = int(ntot * p['prob'])
            trig_dmg+=hit(ad,p['r'],eamp)*n; parts.append(f"{d_}×{n}")
        elif 'hpi' in p:
            n=ntot//p['hpi']
            trig_dmg+=hit(ad,p['r'],eamp)*n; parts.append(f"{d_}×{n}")
        elif 'stk' in p:
            # 叠层触发: 破片/撕裂
            if st in ('summond_sword','lightning','laser'):
                nt_ = nss
            elif st == 'star':
                nt_ = nss + njc  # 星流: 幻影剑+次元斩都算
            elif st in ('judgement_cut','mixed','mixed_d'):
                nt_ = njc
            elif st in ('slash_effect',):
                nt_ = nse
            else:
                nt_ = ntot
            if nt_>=p['stk']:
                if 'n' in p:
                    trig_dmg+=hit(ad,p['r'],eamp)*p['n']; parts.append(d_)
                else:
                    trig_dmg+=hit(ad,p['r'],eamp); parts.append(d_)
        elif 'n' in p:
            trig_dmg+=hit(ad,p['r'],eamp)*p['n']; parts.append(d_)
        elif 'r' in p:
            trig_dmg+=hit(ad,p['r'],eamp); parts.append(d_)

    # 专属SE
    exd=0.0
    if b.ex and b.ex in EXSE:
        e=EXSE[b.ex]
        if 'flat' in e: exd+=e['flat']
        if 'fn' in e: exd+=e['fn'](ad)

    fill=n10+p10
    ts=sas+fill+trig_dmg+exd
    tm=sam+fill*5+exd
    return {
        'amp':round(eamp,2), 'sas':round(sas,1), 'sam':round(sam,1),
        'n10':round(n10,1), 'p10':round(p10,1),
        'trig':round(trig_dmg,1), 'ex':round(exd,1),
        'fill':round(fill,1),
        'ts':round(ts,1), 'tm':round(tm,1),
        'dps':round(ts/2,1), 'dps5':round(tm/2,1),
        'parts':'  '.join(parts)
    }

# ─── 主流程 ───
all_res=[]
for b in BLADES:
    st=b.sa.get('t','?')
    pool=set()
    has_sa = st != '?'
    if not has_sa:
        for s in SE:
            if s[1]=='trig' and s[2]=='any':
                pool.add(s[0])
    else:
        for s in SE:
            n,cat,at,p=s
            if cat=='amp' and match_at(st, at): pool.add(n)
            elif cat=='buff' and match_at(st, at): pool.add(n)
            elif cat=='trig':
                if at in ('any','sa'): pool.add(n)
                elif match_at(st, at): pool.add(n)
    # 补通用触发
    for s in SE:
        if s[0] not in pool and s[1]=='trig' and s[2]=='any':
            pool.add(s[0])
    pool=list(pool)
    # 不够4个就补
    if len(pool)<4:
        for s in SE:
            if s[0] not in pool: pool.append(s[0])
            if len(pool)>=4: break

    combos=list(itertools.combinations(pool,4))
    scored=[]
    for c in combos:
        d=eval_combo(b,list(c))
        scored.append((d['dps'],c,d))
    scored.sort(key=lambda x:x[0],reverse=True)
    all_res.append((b,scored[:5]))

# ─── 输出报告 ───
R=[]
R.append("# Recasting2 个体SE穷举报告（Top5/刀）")
R.append("> 循环: SA起手 + 10普攻(r=1.0) + 10幻影剑(r=0.2)  |  满Buff常驻")
R.append("")
R.append("## 个体SE池(L5)")
R.append("|SE|类|匹配|效果|")
R.append("|---|---|---|---|")
for s in SE:
    n,cat,at,p=s
    if cat=='amp': R.append(f"|{n}|+{p['v']}|{at}|")
    elif cat=='buff': R.append(f"|{n}|+{p['v']}|{at}|{p['d']}|")
    else: R.append(f"|{n}|trig|{at}|{p['d']}|")
R.append("")

# 各刀Top5
for idx,(b,t5) in enumerate(all_res):
    R.append(f"---\n## {b.name} | AD={b.ad} Dp={b.dp} SA={b.sa_n}")
    R.append("|#|SE组合|+amp|SA单|普10|幻10|SE触发|专属|合计单|DPS|DPS5|明细|")
    R.append("|---|---|---|---|---|---|---|---|---|---|---|")
    for ri,(dps,c,d) in enumerate(t5):
        s="+".join(c)
        R.append(f"|{ri+1}|{s}|+{d['amp']}|{d['sas']}|{d['n10']}|{d['p10']}|{d['trig']}|{d['ex']}|{d['ts']}|{d['dps']}|{d['dps5']}|{d['parts']}|")
    R.append("")

# 总排名
R.append("---\n## 全刀对单DPS排名(最佳组合)")
R.append("|#|刀|DPS|最佳SE|SA|填充|SE追|专属|")
R.append("|---|---|---|---|---|---|---|---|")
all_best=[]
best_combo_map = {}
for b,t5 in all_res:
    if t5:
        all_best.append((t5[0][2]['dps'],b,t5[0][2]))
        best_combo_map[b.name] = "+".join(t5[0][1])
all_best.sort(key=lambda x:x[0],reverse=True)
for i,(dps,b,d) in enumerate(all_best):
    cs = best_combo_map.get(b.name, "")
    R.append(f"|{i+1}|{b.name}|{dps}|{cs}|{d['sas']}|{d['fill']}|{d['trig']}|{d['ex']}|")

R.append("")
R.append("---\n## 全刀对群DPS排名(最佳组合)")
R.append("|#|刀|DPS5|最佳SE|SA5|填充5|SE追|")
R.append("|---|---|---|---|---|---|---|")
all_bm=[]
for b,t5 in all_res:
    if t5: all_bm.append((t5[0][2]['dps5'],b,t5[0][2]))
all_bm.sort(key=lambda x:x[0],reverse=True)
for i,(dps,b,d) in enumerate(all_bm):
    cs = best_combo_map.get(b.name, "")
    R.append(f"|{i+1}|{b.name}|{dps}|{cs}|{d['sam']}|{d['fill']*5}|{d['trig']+d['ex']}|")

with open('blade_damage_full_report.md','w',encoding='utf-8') as f:
    f.write('\n'.join(R))
print(f"完成: {len(all_res)}把刀, 每刀Top5")
