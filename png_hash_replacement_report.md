# PNG 哈希比对与 4k 替换报告

生成时间：2026-07-25

## 范围

- 项目 PNG：`src/main/resources/**/*.png`
- 512 参考目录：`D:\Downloads\assets(2025.9.24)\512\slashblade`
- 4k 替换目录：`D:\Downloads\assets_4k`
- 哈希算法：SHA-256
- 排除范围：`build/` 等构建产物

## 汇总

- 项目 PNG 扫描数：110
- 512 参考 PNG 扫描数：27
- 4k PNG 扫描数：27
- 与 512 参考 PNG 哈希完全相同的项目 PNG：19
- 已替换并通过 4k 源文件哈希校验：19
- 替换后哈希实际变化：17
- 替换后哈希未变化：2

`soulblade.png` 和 `textures/item/soul_0.png` 的 4k 源文件与原 512 图哈希一致，因此替换校验通过但文件哈希未变化。

## 替换明细

| 项目文件 | 512 命中源 | 4k 替换源 | 原 SHA-256 | 替换后 SHA-256 | 结果 |
| --- | --- | --- | --- | --- | --- |
| `src\main\resources\assets\recasting\slashblade\brilliant_gold.png` | `blade_t1\blade_tangjian_2\canjin.png` | `blade_t1\blade_tangjian_2\texture.png` | `98944219A28EDBBB45425B6E875DAFDC5551A9D71601265EFA1DBAECFF3C1024` | `1D6B6C12CC27B291AB384E4C357A48D99D2AB6F79986A0AAAF60EB2618583561` | 通过 |
| `src\main\resources\assets\recasting\slashblade\brilliant_tea.png` | `blade_t1\blade_tangdao_2\cancha.png` | `blade_t1\blade_tangdao_2\texture.png` | `9BC3B49AB88AB6E4D8BA8A3170743076EDE7033D7980F28EC23EB15358F14E5A` | `F19DEC25B6C05643326AB3B3DDD7A0EFAFDEA475962F0713C932E7BE0C7A2F46` | 通过 |
| `src\main\resources\assets\recasting\slashblade\broadsword_iron.png` | `blade_t5\blade_jichudao_tie\broadsword_iron.png` | `blade_t5\blade_jichudao_tie\texture.png` | `3C684C48052A43979A04B5EE6DF6715B573398FA38E9A7A9D8FAA696ECDB3562` | `0B771578EEF2386058F1FB605BF63AB94F74B60CE9594AA6773A5D9418CDA6C2` | 通过 |
| `src\main\resources\assets\recasting\slashblade\broadsword_wood.png` | `blade_t6\blade_jichudao_mu\texture.png` | `blade_t6\blade_jichudao_mu\texture.png` | `D532324A1CA4374ACADBA27D94A6634664E32A4387F662E2F4516CA12394CE0C` | `3C31883B9BEB36A9A453572E7227AE6B2576DD214031FC614D6679B788736C83` | 通过 |
| `src\main\resources\assets\recasting\slashblade\broken_white.png` | `blade_t4\broken_white\broken_white.png` | `blade_t4\blade_suibai\texture.png` | `0D61B96849CAB88B23DDCEEDDD188B18FB759E84A7F5B046D67EAAF4BA200369` | `1DAB6E47F4CBC0D4BFBFBC1B62D72FA1BF5E24A98C0AD2F379C85A6E6495BB2A` | 通过 |
| `src\main\resources\assets\recasting\slashblade\cang_jing.png` | `blade_t2\blade_cangjin\texture.png` | `blade_t2\blade_cangjin\texture.png` | `50E01FA872CB536B22BEE0635B291B3A7E240CC9DFD88EAEA3704A225EA1642F` | `0F1D19A1E6B2A70C829CAF965CE7C4E17EB14345DE0D760A726655722F285C76` | 通过 |
| `src\main\resources\assets\recasting\slashblade\green_blade_iron.png` | `blade_t5\blade_jichujian_tie\green_blade_iron.png` | `blade_t5\blade_jichujian_tie\texture.png` | `33D01F3A090C6CB2BD3290B5FFC5D252D8B1C9E4F3E35904EBB7F766DC19818C` | `0EF17E8EE19BC4EEAFEC473CD34C73570FCF2A5073362EE059112653E66C8263` | 通过 |
| `src\main\resources\assets\recasting\slashblade\green_blade_wood.png` | `blade_t6\blade_jichujian_mu\texture.png` | `blade_t6\blade_jichujian_mu\texture.png` | `8314D524799ACE863FEE5E3A693C29BE42F72326AC38FC44F1C458D7240045F9` | `3B2AA5942E12D05A131AF6DDF18F60DBEA98BC40DE775F7A1EBC2EB1C72428EA` | 通过 |
| `src\main\resources\assets\recasting\slashblade\gui_qie.png` | `blade_t1\blade_xuansuoguiqie\texture.png` | `blade_t1\blade_xuansuoguiqie\texture.png` | `3B0A9830E6950E29881881DF8C42979C20E5526BD520D71449AC5A5C73765E58` | `76288875855A239B2C28729429D58A16D42488779053D398C9C09E82B241AA4C` | 通过 |
| `src\main\resources\assets\recasting\slashblade\jing_chu.png` | `blade_t2\blade_jinjicuihun\texture.png` | `blade_t2\blade_jinjicuihun\texture.png` | `85FFD6E7100EC73229B750473FB23480D1DBAF3E42A7DE4DA23C13EE5FC42502` | `2CEE78B56E063D55F1A88661B61F5AB18A32158188DE482ACA153B11ABE39BF4` | 通过 |
| `src\main\resources\assets\recasting\slashblade\jing_hong.png` | `blade_t2\blade_jinghong\texture.png` | `blade_t2\blade_jinghong\texture.png` | `E8E8283F3EBC652225C86F0851F2A364D27111F8871F9BBBEC6129E2FBF43BC8` | `E07176F7C5A75DD2BF69DE848208E77BBBBF7B5DF2029C0E9335DD34CCC33AC1` | 通过 |
| `src\main\resources\assets\recasting\slashblade\shine_gold.png` | `blade_t2\blade_tangjian_1\shine_gold.png` | `blade_t2\blade_tangjian_1\texture.png` | `98944219A28EDBBB45425B6E875DAFDC5551A9D71601265EFA1DBAECFF3C1024` | `1D6B6C12CC27B291AB384E4C357A48D99D2AB6F79986A0AAAF60EB2618583561` | 通过 |
| `src\main\resources\assets\recasting\slashblade\shine_tea.png` | `blade_t2\blade_tangdao_1\shine_tea.png` | `blade_t2\blade_tangdao_1\texture.png` | `9BC3B49AB88AB6E4D8BA8A3170743076EDE7033D7980F28EC23EB15358F14E5A` | `F19DEC25B6C05643326AB3B3DDD7A0EFAFDEA475962F0713C932E7BE0C7A2F46` | 通过 |
| `src\main\resources\assets\recasting\slashblade\soulblade.png` | `blade_t3\blade_yuanshizhihuo\soulblade.png` | `blade_t3\blade_yuanshizhihuo\texture.png` | `FECC813C32135CA860473E947A029469AEA576D235BF4EE4853B6974C9520D90` | `FECC813C32135CA860473E947A029469AEA576D235BF4EE4853B6974C9520D90` | 通过，源文件相同 |
| `src\main\resources\assets\recasting\slashblade\special\tu_wu.png` | `blade_special\blade_tuwu\texture.png` | `blade_special\blade_tuwu\texture.png` | `93DDF57A25CC0BF9D0BAF45A7B9F8FEA969043A2E6BAE236890EA35E2EA2D2EC` | `3E85FA155245800343C00937DB18DCF76378B720A92BE840FC3B1890286FC70E` | 通过 |
| `src\main\resources\assets\recasting\slashblade\special\xuan_yuan_liberated.png` | `blade_special\blade_xuanyuan\texture.png` | `blade_special\blade_xuanyuan\texture.png` | `51975278D6E8DCD90E4CB02A2EFF93B6172FD6CD3653D7BFE95D6761A68D2845` | `8C8F684BF3D535ED6A28837ACCCE65979D4CC5D3AE6AF5B2459A2F9E173FE736` | 通过 |
| `src\main\resources\assets\recasting\slashblade\supreme_pole.png` | `blade_t1\blade_taiji_big\supreme_pole.png` | `blade_t1\blade_taiji_big\texture.png` | `6D2FFFD971E6B606237E45A5E926AC944B6CAD26DA7F827F9B7D75BF4EBD9FF9` | `0D3078581EC07CA4EC4507BD059066528BC99F0DBCB602ACD237816EAB920366` | 通过 |
| `src\main\resources\assets\recasting\slashblade\wind_cloud.png` | `blade_t1\blade_shi_2\wind_cloud.png` | `blade_t1\blade_shi_2\texture.png` | `A2AA1BF40314A0849414C49C58FD469FF64B7324B7AFAAB53C3D5845C8D8B551` | `88EC0284D869EC2554285AFE605349CE6E056B63B8F9FB406A0150DAF39DB8FB` | 通过 |
| `src\main\resources\assets\recasting\textures\item\soul_0.png` | `blade_t3\blade_yuanshizhihuo\soulblade.png` | `blade_t3\blade_yuanshizhihuo\texture.png` | `FECC813C32135CA860473E947A029469AEA576D235BF4EE4853B6974C9520D90` | `FECC813C32135CA860473E947A029469AEA576D235BF4EE4853B6974C9520D90` | 通过，源文件相同 |

## 重名或多源命中

以下项目文件的原始 SHA-256 同时命中多个 512 参考 PNG。替换时按项目文件名优先选择同名参考；若没有同名参考，则选择排序后的第一个参考。对应 4k 源文件哈希一致。

| 项目文件 | 512 命中源 |
| --- | --- |
| `src\main\resources\assets\recasting\slashblade\brilliant_gold.png` | `blade_t1\blade_tangjian_2\canjin.png`; `blade_t2\blade_tangjian_1\shine_gold.png` |
| `src\main\resources\assets\recasting\slashblade\brilliant_tea.png` | `blade_t1\blade_tangdao_2\cancha.png`; `blade_t2\blade_tangdao_1\shine_tea.png` |
| `src\main\resources\assets\recasting\slashblade\shine_gold.png` | `blade_t1\blade_tangjian_2\canjin.png`; `blade_t2\blade_tangjian_1\shine_gold.png` |
| `src\main\resources\assets\recasting\slashblade\shine_tea.png` | `blade_t1\blade_tangdao_2\cancha.png`; `blade_t2\blade_tangdao_1\shine_tea.png` |

## 手动映射

`blade_t4\broken_white\broken_white.png` 在 4k 目录中没有同相对路径或同文件名 PNG。4k 包内同层级只存在 `blade_t4\blade_suibai\texture.png`，本次将它作为 `broken_white.png` 的替换源，并已通过替换后哈希校验。
