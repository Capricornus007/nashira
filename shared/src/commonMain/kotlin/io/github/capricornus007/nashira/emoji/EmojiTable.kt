package io.github.capricornus007.nashira.emoji
// 由 tools/emoji/gen_emoji_table.py 產生 —— 不要手改，改請改腳本重跑。
// 來源：emojibase-data 17.0.0（MIT；底層為 Unicode CLDR / emoji-test.txt）
//   en/data.json ＋ zh-hant/data.json ＋ en/shortcodes/github.json ＋ meta/groups.json
// 共 1914 筆，9 個分塊。每列：group|hexcode|glyph|en|zh|tokens|skins

/** 分類名稱（索引＝group id）。UI 的分類 tab 用 glyph 不用文字，這裡留給需要時。 */
internal val EMOJI_GROUP_NAMES: List<String> = listOf("smileys-emotion", "people-body", "component", "animals-nature", "food-drink", "travel-places", "activities", "objects", "symbols", "flags")

private const val EMOJI_ROWS_0 = """
0|1F600|😀|grinning face|笑臉|cheerful cheery face grin grinning happy laugh nice smile smiling teeth grinning face 笑臉 微笑 欣喜 笑容 臉 露齒而笑|
0|1F603|😃|grinning face with big eyes|大笑|awesome big eyes face grin grinning happy mouth open smile smiling teeth yay grinning face with big eyes smiley 大笑 呵呵 咧嘴大笑 哈哈 笑臉 臉|
0|1F604|😄|grinning face with smiling eyes|呵呵|eye eyes face grin grinning happy laugh lol mouth open smile smiling grinning face with smiling eyes 呵呵 哈哈 笑臉 臉|
0|1F601|😁|beaming face with smiling eyes|嘻嘻|beaming eye eyes face grin grinning happy nice smile smiling teeth beaming face with smiling eyes 嘻嘻 笑臉 笑顏逐開 臉 露齒而笑|
0|1F606|😆|grinning squinting face|狂笑|closed eyes face grinning haha hahaha happy laugh lol mouth open rofl smile smiling squinting grinning squinting face laughing satisfied 狂笑 呵呵 哈哈 好笑 笑口大開 笑臉 臉|
0|1F605|😅|grinning face with sweat|汗|cold dejected excited face grinning mouth nervous open smile smiling stress stressed sweat grinning face with sweat sweat_smile 汗 冒冷汗 笑臉冒冷汗 緊張 臉 苦笑|
0|1F923|🤣|rolling on the floor laughing|笑翻了|crying face floor funny haha happy hehe hilarious joy laugh lmao lol rofl roflmao rolling tear rolling on the floor laughing 笑翻了 lol 哭笑不得 捧腹 笑 笑到眼淚出來 翻 臉|
0|1F602|😂|face with tears of joy|感動|crying face feels funny haha happy hehe hilarious joy laugh lmao lol rofl roflmao tear face with tears of joy 感動 呵呵 哈哈 哭笑不得 喜極而泣 大笑 好笑 極度滑稽 滑稽 臉|
0|1F642|🙂|slightly smiling face|呆呆笑|face happy slightly smile smiling slightly smiling face slightly_smiling_face 呆呆笑 微笑 快樂 笑臉 臉|
0|1F643|🙃|upside-down face|顛倒臉|face hehe smile upside-down upside-down face upside_down_face 顛倒臉 亂了套 臉 臉上下顛倒|
0|1FAE0|🫠|melting face|融化的臉|disappear dissolve embarrassed face haha heat hot liquid lol melt melting sarcasm sarcastic melting face melting_face 融化的臉 哈哈 大笑 尷尬 液態的臉 溶解的臉 炎熱 熱 融化 融化中 諷刺|
0|1F609|😉|winking face|眨眼|face flirt heartbreaker sexy slide tease wink winking winks winking face 眨眼 傷心 臉 表情 調情|
0|1F60A|😊|smiling face with smiling eyes|微笑|blush eye eyes face glad satisfied smile smiling smiling face with smiling eyes 微笑 眼睛也在笑 笑臉 臉|
0|1F607|😇|smiling face with halo|天使笑臉|angel angelic angels blessed face fairy fairytale fantasy halo happy innocent peaceful smile smiling spirit tale smiling face with halo 天使笑臉 光暈 天真無邪 純真笑臉 臉|
0|1F970|🥰|smiling face with hearts|三個愛心的笑臉|3 adore crush face heart hearts ily love romance smile smiling you smiling face with hearts smiling_face_with_three_hearts 三個愛心的笑臉 一見鐘情 三顆心的笑臉 喜歡 愛 愛慕 戀愛了 我愛你 笑 笑臉 臉 迷戀|
0|1F60D|😍|smiling face with heart-eyes|花痴|143 bae eye face feels heart-eyes hearts ily kisses love romance romantic smile xoxo smiling face with heart-eyes heart_eyes 花痴 心花怒放 戀愛中 臉|
0|1F929|🤩|star-struck|眼冒星星|excited eyes face grinning smile star starry-eyed wow star-struck star_struck 眼冒星星 星星 滿眼星光 眼睛 笑臉 臉 讚嘆 追星 露齒笑|
0|1F618|😘|face blowing a kiss|親親飛吻|adorbs bae blowing face flirt heart ily kiss love lover miss muah romantic smooch xoxo you face blowing a kiss kissing_heart 親親飛吻 女朋友 我愛你 擁抱親吻 男朋友 臉 親親 飛吻|
0|1F617|😗|kissing face|親親|143 date dating face flirt ily kiss love smooch smooches xoxo you kissing face kissing 親親 嘟嘴 我愛你 擁抱親吻 臉 表情 親嘴 親臉|
0|263A|☺️|smiling face|輕鬆笑臉|face happy outlined relaxed smile smiling smiling face 輕鬆笑臉 快樂 放鬆一笑 臉|
0|1F61A|😚|kissing face with closed eyes|瞇眼親親|143 bae blush closed date dating eye eyes face flirt ily kisses kissing smooches xoxo kissing face with closed eyes kissing_closed_eyes 瞇眼親親 臉 親親 閉眼親吻|
0|1F619|😙|kissing face with smiling eyes|笑臉親親|143 closed date dating eye eyes face flirt ily kiss kisses kissing love night smile smiling kissing face with smiling eyes kissing_smiling_eyes 笑臉親親 笑著親臉 臉 親親|
0|1F972|🥲|smiling face with tear|微笑帶淚|face glad grateful happy joy pain proud relieved smile smiley smiling tear touched smiling face with tear smiling_face_with_tear 微笑帶淚 感動 感恩 感激 流淚 淚 痛苦 表情符號 開心 驕傲 高興 鬆一口氣|
0|1F60B|😋|face savoring food|口水|delicious eat face food full hungry savor smile smiling tasty um yum yummy face savoring food 口水 回味 夠味 好吃 臉|
0|1F61B|😛|face with tongue|嘿嘿|awesome cool face nice party stuck-out sweet tongue face with tongue stuck_out_tongue 嘿嘿 吐舌頭 臉|
0|1F61C|😜|winking face with tongue|吐舌頭|crazy epic eye face funny joke loopy nutty party stuck-out tongue wacky weirdo wink winking yolo winking face with tongue stuck_out_tongue_winking_eye 吐舌頭 嘿嘿 眨眼吐舌 臉 頑皮|
0|1F92A|🤪|zany face|發瘋|crazy eye eyes face goofy large small zany zany face zany_face 發瘋 大小眼 瘋狂的臉 瘋臉|
0|1F61D|😝|squinting face with tongue|眨眼吐舌頭|closed eye eyes face gross horrible omg squinting stuck-out taste tongue whatever yolo squinting face with tongue stuck_out_tongue_closed_eyes 眨眼吐舌頭 omg 吐舌頭 嘿嘿 我的媽 臉|
0|1F911|🤑|money-mouth face|發財|face money money-mouth mouth paid money-mouth face money_mouth_face 發財 臉 見錢眼開|
0|1F917|🤗|smiling face with open hands|抱抱|face hands hug hugging open smiling smiling face with open hands hugs 抱抱 擁抱 臉|
0|1F92D|🤭|face with hand over mouth|手蓋住嘴巴|face giggle giggling hand mouth oops realization secret shock sudden surprise whoops face with hand over mouth hand_over_mouth 手蓋住嘴巴 傻笑 咯咯 哎喲 掩嘴笑 驚喜|
0|1FAE2|🫢|face with open eyes and hand over mouth|張眼摀嘴的臉|amazement awe disbelief embarrass eyes face gasp hand mouth omg open over quiet scared shock surprise face with open eyes and hand over mouth face_with_open_eyes_and_hand_over_mouth 張眼摀嘴的臉 不敢相信 倒抽氣 嚇到 天啊 尷尬 敬畏 說不出話 驚嚇 驚奇 驚訝|
0|1FAE3|🫣|face with peeking eye|一眼偷看的臉|captivated embarrass eye face hide hiding peek peeking peep scared shy stare face with peeking eye face_with_peeking_eye 一眼偷看的臉 偷看 偷瞄 凝視 害怕 害羞 注視 窺視 著迷 躲藏 躲起來 難為情|
0|1F92B|🤫|shushing face|噓|face quiet shh shush shushing shushing face shushing_face 噓 安靜|
0|1F914|🤔|thinking face|思考|chin consider face hmm ponder pondering thinking wondering thinking face 思考 沈思 臉 表情|
0|1FAE1|🫡|saluting face|敬禮的臉|face good luck ma’am ok respect salute saluting sir troops yes saluting face saluting_face 敬禮的臉 尊重 敬禮 是 是的 祝好運 遵命 部隊 陽光|
0|1F910|🤐|zipper-mouth face|閉嘴|face keep mouth quiet secret shut zip zipper zipper-mouth zipper-mouth face zipper_mouth_face 閉嘴 不能講 住嘴 嘴上拉鍊 臉|
0|1F928|🤨|face with raised eyebrow|挑眉驚訝|disapproval disbelief distrust emoji eyebrow face hmm mild raised skeptic skeptical skepticism surprise what face with raised eyebrow raised_eyebrow 挑眉驚訝 不相信 不贊同 意外 懷疑 提眉 揚眉 臉|
0|1F610|😐️|neutral face|無語|awkward blank deadpan expressionless face fine jealous meh neutral oh shade straight unamused unhappy unimpressed whatever neutral face neutral_face 無語 無反應 臉 表情 面無表情|
0|1F611|😑|expressionless face|面無表情|awkward dead expressionless face fine inexpressive jealous meh not oh omg straight uh unhappy unimpressed whatever expressionless face 面無表情 撲克臉 無語 臉|
0|1F636|😶|face without mouth|沉默|awkward blank expressionless face mouth mouthless mute quiet secret silence silent speechless face without mouth no_mouth 沉默 安靜 無嘴的臉 無語 臉 表情 面無表親|
0|1FAE5|🫥|dotted line face|虛線的臉|depressed disappear dotted face hidden hide introvert invisible line meh whatever wtv dotted line face dotted_line_face 虛線的臉 內向 失望 沮喪 無所謂 躲起來 透明人 隨便 隱形|
0|1F636-200D-1F32B-FE0F|😶‍🌫️|face in clouds|茫茫然|absentminded clouds face fog head face in clouds face_in_clouds 茫茫然 一頭霧水|
0|1F60F|😏|smirking face|冷笑|boss dapper face flirt homie kidding leer shade slick sly smirk smug snicker suave suspicious swag smirking face 冷笑 假笑 臉|
0|1F612|😒|unamused face|不高興|... bored face fine jealous jel jelly pissed smh ugh uhh unamused unhappy weird whatever unamused face 不高興 不開心 嫉妒 臉|
0|1F644|🙄|face with rolling eyes|翻白眼|eyeroll eyes face rolling shade ugh whatever face with rolling eyes roll_eyes 翻白眼 不屑 臉 鄙視 隨便|
0|1F62C|😬|grimacing face|咬牙|awk awkward dentist face grimace grimacing grinning smile smiling grimacing face 咬牙 咬牙切齒 臉 露齒而笑 鬼臉|
0|1F62E-200D-1F4A8|😮‍💨|face exhaling|鬆一口氣|blow blowing exhale exhaling exhausted face gasp groan relief sigh smiley smoke whisper whistle face exhaling face_exhaling 鬆一口氣 倒抽一口氣 吹 吹氣 呻吟 嘆氣 累 表情符號|
0|1F925|🤥|lying face|鼻子變長了|face liar lie lying pinocchio lying face lying_face 鼻子變長了 小木偶 臉 說謊 變 長 鼻|
0|1FAE8|🫨|shaking face|動搖的表情|crazy daze earthquake face omg panic shaking shock surprise vibrate whoa wow shaking face shaking_face 動搖的表情 動搖 地震 抖動 搖動 表情 震動 驚嚇，天啊，驚慌，暈頭轉向，震動，地震，搖晃，哇，瘋狂，驚喜，驚訝|
0|1F642-200D-2194-FE0F|🙂‍↔️|head shaking horizontally|左右搖頭|head horizontally no shake shaking head shaking horizontally 左右搖頭 不，搖頭|
0|1F642-200D-2195-FE0F|🙂‍↕️|head shaking vertically|上下點頭|head nod shaking vertically yes head shaking vertically 上下點頭 點頭，是|
0|1F60C|😌|relieved face|鬆了口氣|calm face peace relief relieved zen relieved face 鬆了口氣 放下心 放鬆 臉 解脫|
0|1F614|😔|pensive face|沉思|awful bored dejected died disappointed face losing lost pensive sad sucks pensive face 沉思 思考 深思 臉|
0|1F62A|😪|sleepy face|睏|crying face good night sad sleep sleeping sleepy tired sleepy face 睏 打瞌睡 掉淚 臉 面臉倦容|
0|1F924|🤤|drooling face|流口水|drooling face drooling face drooling_face 流口水 口 垂涎 水 流 臉|
0|1F634|😴|sleeping face|睡著了|bed bedtime face good goodnight nap night sleep sleeping tired whatever yawn zzz sleeping face 睡著了 夜裏 小歇 想睡 打呼 打瞌睡 晚安 睡臉 累了 臉|
0|1FAE9|🫩|face with bags under eyes|臉上有眼袋|bags bored exhausted eyes face fatigued late sleepy tired weary face with bags under eyes 臉上有眼袋 疲勞的 睏了 筋疲力盡|
0|1F637|😷|face with medical mask|口罩|cold dentist dermatologist doctor dr face germs mask medical medicine sick face with medical mask 口罩 戴口罩的臉 牙醫 生病 病菌 皮膚科 臉 醫生|
0|1F912|🤒|face with thermometer|含溫度計|face ill sick thermometer face with thermometer face_with_thermometer 含溫度計 溫度計 生病 臉|
0|1F915|🤕|face with head-bandage|包繃帶|bandage face head-bandage hurt injury ouch face with head-bandage face_with_head_bandage 包繃帶 受傷 臉 頭綁繃帶的臉|
0|1F922|🤢|nauseated face|想吐|face gross nasty nauseated sick vomit nauseated face nauseated_face 想吐 吐 噁心 想 臉|
0|1F92E|🤮|face vomiting|狂吐中|barf ew face gross puke sick spew throw up vomit vomiting face vomiting vomiting_face 狂吐中 吐 吐臉 嘔吐 噁心 病懨懨 難受|
0|1F927|🤧|sneezing face|打噴嚏|face fever flu gesundheit sick sneeze sneezing sneezing face sneezing_face 打噴嚏 保重 噴 嚏 感冒 打 臉|
0|1F975|🥵|hot face|熱臉|dying face feverish heat hot panting red-faced stroke sweating tongue hot face hot_face 熱臉 中暑 冒汗 吐舌 好熱 死去 流汗 熱 發熱 發燒 臉 臉紅|
0|1F976|🥶|cold face|冷臉|blue blue-faced cold face freezing frostbite icicles subzero teeth cold face cold_face 冷臉 冰凍 冷 冷面 凍傷 凍瘡 很冷 結霜 臉 臉色發青 藍色 藍色的臉|
0|1F974|🥴|woozy face|頭昏眼花|dizzy drunk eyes face intoxicated mouth tipsy uneven wavy woozy woozy face woozy_face 頭昏眼花 喝多了 喝醉 嘴巴波浪 大小眼 微醺 波浪嘴 雙眼不平 頭昏 頭暈|
0|1F635|😵|face with crossed-out eyes|頭暈目眩|crossed-out dead dizzy eyes face feels knocked out sick tired face with crossed-out eyes dizzy_face 頭暈目眩 困惑 臉 表情 頭昏|
0|1F635-200D-1F4AB|😵‍💫|face with spiral eyes|目眩頭暈|confused dizzy eyes face hypnotized omg smiley spiral trouble whoa woah woozy face with spiral eyes face_with_spiral_eyes 目眩頭暈 不懂 催眠 呃 哇 困惑 天啊 暈 暈眩 煩惱 眩暈 頭暈|
0|1F92F|🤯|exploding head|一個頭兩個大|blown explode exploding head mind mindblown no shocked way exploding head exploding_head 一個頭兩個大 爆炸頭 驚嚇|
0|1F920|🤠|cowboy hat face|牛仔|cowboy cowgirl face hat cowboy hat face cowboy_hat_face 牛仔 仔 牛 臉|
0|1F973|🥳|partying face|慶祝的表情|bday birthday celebrate celebration excited face happy hat hooray horn party partying partying face partying_face 慶祝的表情 喝采 帽子 慶祝 歡呼 派對 生日 紙喇叭 臉 興奮|
0|1F978|🥸|disguised face|假扮的臉|disguise eyebrow face glasses incognito moustache mustache nose person spy tache tash disguised face disguised_face 假扮的臉 人 假扮 假鼻子 偽裝 眉毛 眼鏡 間諜 鬍子|
0|1F60E|😎|smiling face with sunglasses|墨鏡|awesome beach bright bro chilling cool face rad relaxed shades slay smile style sunglasses swag win smiling face with sunglasses 墨鏡 太陽眼鏡 戴著墨鏡的笑臉 眼鏡 臉 自吹自擂 酷|
0|1F913|🤓|nerd face|搞怪|brainy clever expert face geek gifted glasses intelligent nerd smart nerd face nerd_face 搞怪 宅 怪人 當阿宅 聰明，頭腦好，有智慧，專家，有天份，阿宅，傻，眼鏡，宅男，宅女，臉 臉|
0|1F9D0|🧐|face with monocle|單片眼鏡|classy face fancy monocle rich stuffy wealthy face with monocle monocle_face 單片眼鏡 古板臉 奢華 有錢人 病厭厭 老古板|
0|1F615|😕|confused face|困擾|befuddled confused confusing dunno face frown hm meh not sad sorry sure confused face 困擾 不確定 困惑面容 臉 面有難色|
0|1FAE4|🫤|face with diagonal mouth|歪嘴的臉|confused confusion diagonal disappointed doubt doubtful face frustrated frustration meh mouth skeptical unsure whatever wtv face with diagonal mouth face_with_diagonal_mouth 歪嘴的臉 不確定 受挫 困惑 失望 懷疑 挫折 無所謂 疑惑 隨便|
0|1F61F|😟|worried face|擔心|anxious butterflies face nerves nervous sad stress stressed surprised worried worry worried face 擔心 憂心忡忡 臉|
0|1F641|🙁|slightly frowning face|不開心|face frown frowning sad slightly slightly frowning face slightly_frowning_face 不開心 壞心情 微慍 皺眉 臉|
0|2639|☹️|frowning face|不滿意|face frown frowning sad frowning face frowning_face 不滿意 不爽 悲哀 皺眉 臉|
0|1F62E|😮|face with open mouth|驚訝|believe face forgot mouth omg open shocked surprised sympathy unbelievable unreal whoa wow you face with open mouth open_mouth 驚訝 不可能 啊 我不信 臉|
0|1F62F|😯|hushed face|哦|epic face hushed omg stunned surprised whoa woah hushed face 哦 意外 臉 說不出話來 驚訝|
0|1F632|😲|astonished face|震驚|astonished cost face no omg shocked totally way astonished face 震驚 怎麼可能 我的天 臉 驚|
0|1F633|😳|flushed face|愛慕|amazed awkward crazy dazed dead disbelief embarrassed face flushed geez heat hot impressed jeez what wow flushed face 愛慕 不可置信 太扯了 害羞 怎麼可能 臉 臉紅 茫然|
0|1FAEA|🫪|distorted face|變形的臉|anxiety bloated panic shocked surprised vulnerable distorted face 變形的臉 恐慌 慌張 焦慮 脆弱 腫脹 驚喜 驚嚇|
0|1F97A|🥺|pleading face|請求的臉|begging big eyes face mercy not pleading please pretty puppy sad why pleading face pleading_face 請求的臉 大眼睛 懇求 拜託 求求你 無辜臉 請求臉孔|
0|1F979|🥹|face holding back tears|強忍淚水的臉|admiration aww back cry embarrassed face feelings grateful gratitude holding joy please proud resist sad tears face holding back tears face_holding_back_tears 強忍淚水的臉 仰慕 傷心 哭泣 喜極而泣 悲傷 感受 感激 感謝 抗拒 拜託 生氣 窩心 難為情 難過 驕傲|
0|1F626|😦|frowning face with open mouth|啊|caught face frown frowning guard mouth open scared scary surprise what wow frowning face with open mouth 啊 始料未及 張嘴皺眉 目瞪口呆 臉|
0|1F627|😧|anguished face|痛|anguished face forgot scared scary stressed surprise unhappy what wow anguished face 痛 痛苦 臉 難受|
0|1F628|😨|fearful face|可怕|afraid anxious blame face fear fearful scared worried fearful face 可怕 害怕 幾乎落淚的臉 憂心 責備|
0|1F630|😰|anxious face with sweat|冒汗|anxious blue cold eek face mouth nervous open rushed scared sweat yikes anxious face with sweat cold_sweat 冒汗 發冷 緊張 臉色發青 面有菜色|
0|1F625|😥|sad but relieved face|流汗|anxious call close complicated disappointed face not relieved sad sweat time whew sad but relieved face disappointed_relieved 流汗 倖免於難 失望卻解脫 好險 沮喪 臉 鬆了口氣|
0|1F622|😢|crying face|淚|awful cry crying face feels miss sad tear triste unhappy crying face 淚 哭臉 想念 流淚 糟透了 臉|
0|1F62D|😭|loudly crying face|哭|bawling cry crying face loudly sad sob tear tears unhappy loudly crying face 哭 哭臉 大哭 淚 淚流滿面 臉|
0|1F631|😱|face screaming in fear|嚇死了|epic face fear fearful munch scared scream screamer screaming shocked surprised woah face screaming in fear 嚇死了 尖叫 恐怖 驚叫|
0|1F616|😖|confounded face|困惑|annoyed confounded confused cringe distraught face feels frustrated mad sad confounded face 困惑 侷促不安 滿臉困惑 焦頭爛額 臉|
0|1F623|😣|persevering face|痛苦|concentrate concentration face focus headache persevere persevering persevering face 痛苦 堅忍 專心 忍痛中 忍耐 臉 頭痛|
0|1F61E|😞|disappointed face|失望|awful blame dejected disappointed face fail losing sad unhappy disappointed face 失望 很糟 沮喪 臉 輸了|
0|1F613|😓|downcast face with sweat|冷汗|close cold downcast face feels headache nervous sad scared sweat yikes downcast face with sweat 冷汗 冒冷汗的臉 臉 表情|
0|1F629|😩|weary face|唉!|crying face fail feels hungry mad nooo sad sleepy tired unhappy weary weary face 唉! 疲勞 疲憊 累 臉|
0|1F62B|😫|tired face|累|cost face feels nap sad sneeze tired tired face tired_face 累 嘆氣 滿臉倦容 疲勞 疲憊 臉|
0|1F971|🥱|yawning face|呵欠|bedtime bored face goodnight nap night sleep sleepy tired whatever yawn yawning zzz yawning face yawning_face 呵欠 小睡 想睡 想睡的臉 晚上 無聊 疲勞 瞌睡連連 都好|
0|1F624|😤|face with steam from nose|傲慢|anger angry face feels fume fuming furious fury mad nose steam triumph unhappy won face with steam from nose 傲慢 怒氣沖沖 揚眉吐氣 氣瘋了 生氣 發怒 臉|
0|1F621|😡|enraged face|生氣|anger angry enraged face feels mad maddening pouting rage red shade unhappy upset enraged face pout 生氣 怒 漲紅了臉 發火 發飆 臉|
0|1F620|😠|angry face|不爽|anger angry blame face feels frustrated mad maddening rage shade unhappy upset angry face 不爽 火大 生氣 臉 責怪|
0|1F92C|🤬|face with symbols on mouth|嘴上有符號的表情|censor cursing cussing face mad mouth pissed swearing symbols face with symbols on mouth cursing_face 嘴上有符號的表情 不爽 發怒 發誓 詛咒|
0|1F608|😈|smiling face with horns|惡魔的笑|demon devil evil face fairy fairytale fantasy horns purple shade smile smiling tale smiling face with horns smiling_imp 惡魔的笑 邪惡的笑 陰暗地笑|
0|1F47F|👿|angry face with horns|惡魔|angry demon devil evil face fairy fairytale fantasy horns imp mischievous purple shade tale angry face with horns 惡魔 怒氣惡臉 臉 表情 邪惡|
0|1F480|💀|skull|骷髏頭|body dead death face fairy fairytale i’m lmao monster tale yolo skull 骷髏頭 怪物 頭骨 骷髏|
0|2620|☠️|skull and crossbones|交叉骷髏頭|bone crossbones dead death face monster skull skull and crossbones skull_and_crossbones 交叉骷髏頭 死亡 海盜 頭殼 骷髏頭|
0|1F4A9|💩|pile of poo|大便|bs comic doo dung face fml monster pile poo poop smelly smh stink stinks stinky turd pile of poo hankey shit 大便 便便 臭臭|
0|1F921|🤡|clown face|小丑|clown face clown face clown_face 小丑 丑 小 臉|
0|1F479|👹|ogre|魔鬼|creature devil face fairy fairytale fantasy mask monster scary tale ogre japanese_ogre 魔鬼 妖怪 惡鬼 表情 面具 食人巨妖|
0|1F47A|👺|goblin|天狗|angry creature face fairy fairytale fantasy mask mean monster tale goblin japanese_goblin 天狗 妖怪 小妖怪 怪物 面具|
0|1F47B|👻|ghost|鬼|boo creature excited face fairy fairytale fantasy halloween haunting monster scary silly tale ghost 鬼 幽靈 萬聖節 鬧鬼 鬼臉|
0|1F47D|👽️|alien|外星人|creature extraterrestrial face fairy fairytale fantasy monster space tale ufo alien 外星人 et 幽浮|
0|1F47E|👾|alien monster|怪物|alien creature extraterrestrial face fairy fairytale fantasy game gamer games monster pixelated space tale ufo alien monster space_invader 怪物 外星怪物 幽浮 電玩|
0|1F916|🤖|robot|機器人|face monster robot 機器人 機器人|
0|1F63A|😺|grinning cat|哈哈|animal cat face grinning mouth open smile smiling grinning cat smiley_cat 哈哈 呵呵 笑口大開 笑口常開 笑臉|
0|1F638|😸|grinning cat with smiling eyes|微笑的貓臉|animal cat eye eyes face grin grinning smile smiling grinning cat with smiling eyes smile_cat 微笑的貓臉 笑臉|
0|1F639|😹|cat with tears of joy|感動的貓臉|animal cat face joy laugh laughing lol tear tears cat with tears of joy joy_cat 感動的貓臉 又哭又笑 喜極而泣 臉|
0|1F63B|😻|smiling cat with heart-eyes|花痴的貓臉|animal cat eye face heart heart-eyes love smile smiling smiling cat with heart-eyes heart_eyes_cat 花痴的貓臉 心眼的貓臉 心花怒放 臉|
0|1F63C|😼|cat with wry smile|微笑貓臉|animal cat face ironic smile wry cat with wry smile smirk_cat 微笑貓臉 冷嘲 動物 嘲笑的貓臉 嘲諷 笑 臉 貓|
0|1F63D|😽|kissing cat|親親的貓臉|animal cat closed eye eyes face kiss kissing kissing cat kissing_cat 親親的貓臉 動物 臉 親親 閉眼親親的貓臉|
0|1F640|🙀|weary cat|累的貓臉|animal cat face oh surprised weary weary cat scream_cat 累的貓臉 意外 疲勞 疲憊 累|
0|1F63F|😿|crying cat|哭的貓臉|animal cat cry crying face sad tear crying cat crying_cat_face 哭的貓臉 動物 哭 哭臉 淚 臉|
0|1F63E|😾|pouting cat|生氣的貓臉|animal cat face pouting pouting cat pouting_cat 生氣的貓臉 噘嘴的貓臉 怒 生氣 發火 發飆|
0|1F648|🙈|see-no-evil monkey|非禮勿視|embarrassed evil face forbidden forgot gesture hide monkey no omg prohibited scared secret smh watch see-no-evil monkey see_no_evil 非禮勿視 不看 動物 我的天 遮眼|
0|1F649|🙉|hear-no-evil monkey|非禮勿聽|animal ears evil face forbidden gesture hear listen monkey no not prohibited secret shh tmi hear-no-evil monkey hear_no_evil 非禮勿聽 不聽 不許聽 秘密 遮耳|
0|1F64A|🙊|speak-no-evil monkey|非禮勿言|animal evil face forbidden gesture monkey no not oops prohibited quiet secret speak stealth speak-no-evil monkey speak_no_evil 非禮勿言 動物 摀嘴 禁言|
0|1F48C|💌|love letter|情書|heart letter love mail romance valentine love letter love_letter 情書 情人節 愛的箴言|
0|1F498|💘|heart with arrow|丘比特|143 adorbs arrow cupid date emotion heart ily love romance valentine heart with arrow 丘比特 愛 愛神的箭 戀愛|
0|1F49D|💝|heart with ribbon|愛的禮物|143 anniversary emotion heart ily kisses ribbon valentine xoxo heart with ribbon gift_heart 愛的禮物 情人節 送你一顆心|
0|1F496|💖|sparkling heart|開心|143 emotion excited good heart ily kisses morning night sparkle sparkling xoxo sparkling heart sparkling_heart 開心 吻 放閃 晚安 閃亮|
0|1F497|💗|growing heart|心動|143 emotion excited growing heart heartpulse ily kisses muah nervous pulse xoxo growing heart 心動 心撲通跳 我愛你 緊張|
0|1F493|💓|beating heart|心跳|143 beating cardio emotion heart heartbeat ily love pulsating pulse beating heart 心跳 心 愛 愛心跳動|
0|1F49E|💞|revolving hearts|心之舞|143 adorbs anniversary emotion heart hearts revolving revolving hearts revolving_hearts 心之舞 我愛你 舞動的心 週年紀念|
0|1F495|💕|two hearts|心心相印|143 anniversary date dating emotion heart hearts ily kisses love loving two xoxo two hearts two_hearts 心心相印 我愛你 相愛|
0|1F49F|💟|heart decoration|心在框框裡|143 decoration emotion heart hearth purple white heart decoration heart_decoration 心在框框裡 心在框框裏 心型 我愛你 紫心|
0|2763|❣️|heart exclamation|心嘆號|exclamation heart heavy mark punctuation heart exclamation heavy_heart_exclamation 心嘆號 心型驚嘆號|
0|1F494|💔|broken heart|心碎|break broken crushed emotion heart heartbroken lonely sad broken heart broken_heart 心碎 破碎的心|
0|2764-FE0F-200D-1F525|❤️‍🔥|heart on fire|火熱的心|burn fire heart love lust sacred heart on fire heart_on_fire 火熱的心 奉獻 心型 愛 慾望|
0|2764-FE0F-200D-1FA79|❤️‍🩹|mending heart|療心|healthier heart improving mending recovering recuperating well mending heart mending_heart 療心 健康 恢復 療傷|
0|2764|❤️|red heart|愛心|emotion heart love red red heart 愛心 心型|
0|1FA77|🩷|pink heart|粉紅心|143 adorable cute emotion heart ily like love pink special sweet pink heart pink_heart 粉紅心 可愛 喜歡 心型 愛 粉紅 粉紅，愛，可愛，甜蜜，可愛的，特殊，心情，心，喜歡|
0|1F9E1|🧡|orange heart|橘心|143 heart orange orange heart orange_heart 橘心 心型 橘色 橘色心|
0|1F49B|💛|yellow heart|黃心|143 cardiac emotion heart ily love yellow yellow heart yellow_heart 黃心 心型 愛 我愛你|
0|1F49A|💚|green heart|綠心|143 emotion green heart ily love romantic green heart green_heart 綠心 心型 我愛你|
0|1F499|💙|blue heart|藍心|143 blue emotion heart ily love romance blue heart blue_heart 藍心 心型|
0|1FA75|🩵|light blue heart|淺藍心|143 blue cute cyan emotion heart ily light like love sky special teal light blue heart light_blue_heart 淺藍心 心型 淺藍 淺藍，天蘭，喜歡，心情，心，可愛，愛，特殊，藍綠 藍綠 青綠|
0|1F49C|💜|purple heart|紫心|143 bestest emotion heart ily love purple purple heart purple_heart 紫心 心型 愛 我愛你|
0|1F90E|🤎|brown heart|褐心|143 brown heart brown heart brown_heart 褐心 咖啡色 心 愛心 褐 褐色|
0|1F5A4|🖤|black heart|黑心|black evil heart wicked black heart black_heart 黑心 心 邪惡 黑|
0|1FA76|🩶|grey heart|灰色愛心|143 emotion gray grey heart ily love silver slate special grey heart grey_heart 灰色愛心 心型 暗灰色 灰 灰色，特殊，心情，愛心，愛，銀色 石板色 銀|
0|1F90D|🤍|white heart|白心|143 heart white white heart white_heart 白心 心 愛心 白 白色|
0|1F48B|💋|kiss mark|唇印|dating emotion heart kiss kissing lips mark romance sexy kiss mark 唇印 吻 性感 約會 親親|
0|1F4AF|💯|hundred points|滿分|100 a+ agree clearly definitely faithful fleek full hundred keep perfect point score true truth yup hundred points 滿分 100分 絕對|
0|1F4A2|💢|anger symbol|怒|anger angry comic mad symbol upset anger symbol 怒 火大 爆青筋|
0|1FAEF|🫯|fight cloud|打鬥的雲|argument brawl debate disagreement fight ruckus wrestle fight cloud 打鬥的雲 不同意 戰鬥 搏鬥 爭論 辯論 騷動 鬥毆|
0|1F4A5|💥|collision|碰撞|bomb boom collide comic explode collision 碰撞 引爆 炸彈 爆炸|
0|1F4AB|💫|dizzy|暈頭轉向|comic shining shooting star stars dizzy 暈頭轉向 星星 流星 頭暈目眩|
0|1F4A6|💦|sweat droplets|出汗|comic drip droplet droplets drops splashing squirt sweat water wet work workout sweat droplets sweat_drops 出汗 水珠 汗 訓練|
0|1F4A8|💨|dashing away|揚塵而去|away cloud comic dash dashing fart fast go gone gotta running smoke dashing away 揚塵而去 放屁|
0|1F573|🕳️|hole|洞|hole 洞 坑洞|
0|1F4AC|💬|speech balloon|對話框|balloon bubble comic dialog message sms speech talk text typing speech balloon speech_balloon 對話框 泡泡框 簡訊 輸入 輸入氣球|
0|1F441-FE0F-200D-1F5E8-FE0F|👁️‍🗨️|eye in speech bubble|眼睛對話框|balloon bubble eye speech witness eye in speech bubble eye_speech_bubble 眼睛對話框 對話框|
0|1F5E8|🗨️|left speech bubble|黑色對話框|balloon bubble dialog left speech left speech bubble left_speech_bubble 黑色對話框 對話框|
0|1F5EF|🗯️|right anger bubble|爆炸對話框|anger angry balloon bubble mad right right anger bubble right_anger_bubble 爆炸對話框 對話框|
0|1F4AD|💭|thought balloon|心聲對話框|balloon bubble cartoon cloud comic daydream decisions dream idea invent invention realize think thoughts wonder thought balloon thought_balloon 心聲對話框 對話框|
0|1F4A4|💤|ZZZ|睡著|comic good goodnight night sleep sleeping sleepy tired zzz 睡著 zz 想睡 打呼 滑稽 睡了 累癱了|
1|1F44B|👋|waving hand|揮手|bye cya g2g greetings gtg hand hello hey hi later outtie ttfn ttyl wave yo you waving hand 揮手 再見 手 手勢 拜拜 是你嗎 該走了 閃人|1F44B-1F3FB:👋🏻 1F44B-1F3FC:👋🏼 1F44B-1F3FD:👋🏽 1F44B-1F3FE:👋🏾 1F44B-1F3FF:👋🏿
1|1F91A|🤚|raised back of hand|豎起手掌|back backhand hand raised raised back of hand raised_back_of_hand 豎起手掌 反手 手 手掌 掌 豎|1F91A-1F3FB:🤚🏻 1F91A-1F3FC:🤚🏼 1F91A-1F3FD:🤚🏽 1F91A-1F3FE:🤚🏾 1F91A-1F3FF:🤚🏿
1|1F590|🖐️|hand with fingers splayed|停止|finger fingers hand raised splayed stop hand with fingers splayed raised_hand_with_fingers_splayed 停止 對外張開五指|1F590-1F3FB:🖐🏻 1F590-1F3FC:🖐🏼 1F590-1F3FD:🖐🏽 1F590-1F3FE:🖐🏾 1F590-1F3FF:🖐🏿
1|270B|✋️|raised hand|舉手|5 five hand high raised stop raised hand raised_hand 舉手 手 招手 擊掌|270B-1F3FB:✋🏻 270B-1F3FC:✋🏼 270B-1F3FD:✋🏽 270B-1F3FE:✋🏾 270B-1F3FF:✋🏿
1|1F596|🖖|vulcan salute|你好|finger hand hands salute vulcan vulcan salute vulcan_salute 你好 瓦肯式敬禮 生生不息，繁榮昌盛|1F596-1F3FB:🖖🏻 1F596-1F3FC:🖖🏼 1F596-1F3FD:🖖🏽 1F596-1F3FE:🖖🏾 1F596-1F3FF:🖖🏿
1|1FAF1|🫱|rightwards hand|向右的手|hand handshake hold reach right rightward rightwards shake rightwards hand rightwards_hand 向右的手 伸手 右 右手 向右 手 握 握手|1FAF1-1F3FB:🫱🏻 1FAF1-1F3FC:🫱🏼 1FAF1-1F3FD:🫱🏽 1FAF1-1F3FE:🫱🏾 1FAF1-1F3FF:🫱🏿
1|1FAF2|🫲|leftwards hand|向左的手|hand handshake hold left leftward leftwards reach shake leftwards hand leftwards_hand 向左的手 伸手 向左 左 左手 手 握 握手|1FAF2-1F3FB:🫲🏻 1FAF2-1F3FC:🫲🏼 1FAF2-1F3FD:🫲🏽 1FAF2-1F3FE:🫲🏾 1FAF2-1F3FF:🫲🏿
1|1FAF3|🫳|palm down hand|手掌向下|dismiss down drop dropped hand palm pick shoo up palm down hand palm_down_hand 手掌向下 手 打發 掉了 掉落 撿起 撿起來 放下 趕走|1FAF3-1F3FB:🫳🏻 1FAF3-1F3FC:🫳🏼 1FAF3-1F3FD:🫳🏽 1FAF3-1F3FE:🫳🏾 1FAF3-1F3FF:🫳🏿
1|1FAF4|🫴|palm up hand|手掌向上|beckon catch come hand hold know lift me offer palm tell palm up hand palm_up_hand 手掌向上 不知道 來 出示 召喚 告訴我 手 拿 接住 給予 舉起 過來|1FAF4-1F3FB:🫴🏻 1FAF4-1F3FC:🫴🏼 1FAF4-1F3FD:🫴🏽 1FAF4-1F3FE:🫴🏾 1FAF4-1F3FF:🫴🏿
1|1FAF7|🫷|leftwards pushing hand|手向左推|block five halt hand high hold leftward leftwards pause push pushing refuse slap stop wait leftwards pushing hand leftwards_pushing_hand 手向左推 停 向左 往左，推，擊掌，阻擋，暫停，停，停止，等等，手，拒絕 拒絕 推 擊掌 等一下|1FAF7-1F3FB:🫷🏻 1FAF7-1F3FC:🫷🏼 1FAF7-1F3FD:🫷🏽 1FAF7-1F3FE:🫷🏾 1FAF7-1F3FF:🫷🏿
1|1FAF8|🫸|rightwards pushing hand|手向右推|block five halt hand high hold pause push pushing refuse rightward rightwards slap stop wait rightwards pushing hand rightwards_pushing_hand 手向右推 停 向右 往右，推，擊掌，阻擋，暫停，停，停止，等等，手，拒絕 拒絕 推 擊掌 等一下|1FAF8-1F3FB:🫸🏻 1FAF8-1F3FC:🫸🏼 1FAF8-1F3FD:🫸🏽 1FAF8-1F3FE:🫸🏾 1FAF8-1F3FF:🫸🏿
1|1F44C|👌|OK hand|OK 手勢|awesome bet dope fleek fosho got gotcha hand legit ok okay pinch rad sure sweet three ok hand ok_hand ok 手勢 ok ok 手勢 了解 好 手指 沒問題 當然|1F44C-1F3FB:👌🏻 1F44C-1F3FC:👌🏼 1F44C-1F3FD:👌🏽 1F44C-1F3FE:👌🏾 1F44C-1F3FF:👌🏿
1|1F90C|🤌|pinched fingers|捏手指|fingers gesture hand hold huh interrogation patience pinched relax sarcastic ugh what zip pinched fingers pinched_fingers 捏手指 什麼 呃 手勢 捏 疑問 等一下 耐心 蛤 閉嘴|1F90C-1F3FB:🤌🏻 1F90C-1F3FC:🤌🏼 1F90C-1F3FD:🤌🏽 1F90C-1F3FE:🤌🏾 1F90C-1F3FF:🤌🏿
1|1F90F|🤏|pinching hand|捏|amount bit fingers hand little pinching small sort pinching hand pinching_hand 捏 一點點 小 少量 很少 手指|1F90F-1F3FB:🤏🏻 1F90F-1F3FC:🤏🏼 1F90F-1F3FD:🤏🏽 1F90F-1F3FE:🤏🏾 1F90F-1F3FF:🤏🏿
1|270C|✌️|victory hand|勝利|hand peace v victory victory hand 勝利 v 勝利手勢 耶|270C-1F3FB:✌🏻 270C-1F3FC:✌🏼 270C-1F3FD:✌🏽 270C-1F3FE:✌🏾 270C-1F3FF:✌🏿
1|1F91E|🤞|crossed fingers|祝好運|cross crossed finger fingers hand luck crossed fingers crossed_fingers 祝好運 加油 好 祝 運|1F91E-1F3FB:🤞🏻 1F91E-1F3FC:🤞🏼 1F91E-1F3FD:🤞🏽 1F91E-1F3FE:🤞🏾 1F91E-1F3FF:🤞🏿
1|1FAF0|🫰|hand with index finger and thumb crossed|食指和拇指交叉的手|<3 crossed expensive finger hand heart index love money snap thumb hand with index finger and thumb crossed hand_with_index_finger_and_thumb_crossed 食指和拇指交叉的手 <3 彈響指 愛 愛心 手 昂貴 貴的 錢|1FAF0-1F3FB:🫰🏻 1FAF0-1F3FC:🫰🏼 1FAF0-1F3FD:🫰🏽 1FAF0-1F3FE:🫰🏾 1FAF0-1F3FF:🫰🏿
1|1F91F|🤟|love-you gesture|愛你手勢|fingers gesture hand ily love love-you three you love-you gesture love_you_gesture 愛你手勢 ily 三指 愛你 我愛你 手|1F91F-1F3FB:🤟🏻 1F91F-1F3FC:🤟🏼 1F91F-1F3FD:🤟🏽 1F91F-1F3FE:🤟🏾 1F91F-1F3FF:🤟🏿
1|1F918|🤘|sign of the horns|ROCK|finger hand horns rock-on sign sign of the horns metal rock rock 手指 搖滾手勢 搖滾精神 繼續搖滾|1F918-1F3FB:🤘🏻 1F918-1F3FC:🤘🏼 1F918-1F3FD:🤘🏽 1F918-1F3FE:🤘🏾 1F918-1F3FF:🤘🏿
1|1F919|🤙|call me hand|打給我|call hand hang loose me shaka call me hand call_me_hand 打給我 打 打電話 通電話 電話|1F919-1F3FB:🤙🏻 1F919-1F3FC:🤙🏼 1F919-1F3FD:🤙🏽 1F919-1F3FE:🤙🏾 1F919-1F3FF:🤙🏿
1|1F448|👈️|backhand index pointing left|左|backhand finger hand index left point pointing backhand index pointing left point_left 左 反手 手 手指向左 指|1F448-1F3FB:👈🏻 1F448-1F3FC:👈🏼 1F448-1F3FD:👈🏽 1F448-1F3FE:👈🏾 1F448-1F3FF:👈🏿
1|1F449|👉️|backhand index pointing right|右|backhand finger hand index point pointing right backhand index pointing right point_right 右 手 指 指向右方|1F449-1F3FB:👉🏻 1F449-1F3FC:👉🏼 1F449-1F3FD:👉🏽 1F449-1F3FE:👉🏾 1F449-1F3FF:👉🏿
1|1F446|👆️|backhand index pointing up|上|backhand finger hand index point pointing up backhand index pointing up point_up_2 上 手 手指 手指向上 指 食指 食指向上|1F446-1F3FB:👆🏻 1F446-1F3FC:👆🏼 1F446-1F3FD:👆🏽 1F446-1F3FE:👆🏾 1F446-1F3FF:👆🏿
1|1F595|🖕|middle finger|中指|finger hand middle middle finger fu middle_finger 中指 比中指|1F595-1F3FB:🖕🏻 1F595-1F3FC:🖕🏼 1F595-1F3FD:🖕🏽 1F595-1F3FE:🖕🏾 1F595-1F3FF:🖕🏿
1|1F447|👇️|backhand index pointing down|下|backhand down finger hand index point pointing backhand index pointing down point_down 下 手 手指 指 指頭向下 食指向下|1F447-1F3FB:👇🏻 1F447-1F3FC:👇🏼 1F447-1F3FD:👇🏽 1F447-1F3FE:👇🏾 1F447-1F3FF:👇🏿
1|261D|☝️|index pointing up|注意|finger hand index point pointing this up index pointing up point_up 注意 提示 提醒 食指|261D-1F3FB:☝🏻 261D-1F3FC:☝🏼 261D-1F3FD:☝🏽 261D-1F3FE:☝🏾 261D-1F3FF:☝🏿
1|1FAF5|🫵|index pointing at the viewer|食指朝向觀眾|at finger hand index pointing poke viewer you index pointing at the viewer index_pointing_at_the_viewer 食指朝向觀眾 你 對準 您 戳 手 手指 指 指向|1FAF5-1F3FB:🫵🏻 1FAF5-1F3FC:🫵🏼 1FAF5-1F3FD:🫵🏽 1FAF5-1F3FE:🫵🏾 1FAF5-1F3FF:🫵🏿
1|1F44D|👍️|thumbs up|讚|+1 good hand like thumb up yes thumbs up thumbsup 讚 ok 好棒 好耶 我喜歡 我愛 拇指 棒 正點 當然|1F44D-1F3FB:👍🏻 1F44D-1F3FC:👍🏼 1F44D-1F3FD:👍🏽 1F44D-1F3FE:👍🏾 1F44D-1F3FF:👍🏿
1|1F44E|👎️|thumbs down|遜|-1 bad dislike down good hand no nope thumb thumbs thumbs down thumbsdown 遜 不可以 好爛 拇指向下|1F44E-1F3FB:👎🏻 1F44E-1F3FC:👎🏼 1F44E-1F3FD:👎🏽 1F44E-1F3FE:👎🏾 1F44E-1F3FF:👎🏿
1|270A|✊️|raised fist|拳頭|clenched fist hand punch raised solidarity raised fist fist_raised 拳頭 團結 握拳 舉拳|270A-1F3FB:✊🏻 270A-1F3FC:✊🏼 270A-1F3FD:✊🏽 270A-1F3FE:✊🏾 270A-1F3FF:✊🏿
1|1F44A|👊|oncoming fist|出拳|absolutely agree boom bro bruh bump clenched correct fist hand knuckle oncoming pound punch rock ttyl oncoming fist facepunch fist_oncoming 出拳 拳頭 擊拳同意 絕對贊成|1F44A-1F3FB:👊🏻 1F44A-1F3FC:👊🏼 1F44A-1F3FD:👊🏽 1F44A-1F3FE:👊🏾 1F44A-1F3FF:👊🏿
1|1F91B|🤛|left-facing fist|握右拳|fist left-facing leftwards left-facing fist fist_left 握右拳 右 拳 握|1F91B-1F3FB:🤛🏻 1F91B-1F3FC:🤛🏼 1F91B-1F3FD:🤛🏽 1F91B-1F3FE:🤛🏾 1F91B-1F3FF:🤛🏿
1|1F91C|🤜|right-facing fist|握左拳|fist right-facing rightwards right-facing fist fist_right 握左拳 左 拳 握 握拳|1F91C-1F3FB:🤜🏻 1F91C-1F3FC:🤜🏼 1F91C-1F3FD:🤜🏽 1F91C-1F3FE:🤜🏾 1F91C-1F3FF:🤜🏿
"""

private const val EMOJI_ROWS_1 = """
1|1F44F|👏|clapping hands|鼓掌|applause approval awesome clap congrats congratulations excited good great hand homie job nice prayed well yay clapping hands 鼓掌 做得好 恭賀 拍手 贊同|1F44F-1F3FB:👏🏻 1F44F-1F3FC:👏🏼 1F44F-1F3FD:👏🏽 1F44F-1F3FE:👏🏾 1F44F-1F3FF:👏🏿
1|1F64C|🙌|raising hands|歡呼|celebration gesture hand hands hooray praise raised raising raising hands raised_hands 歡呼 慶祝 舉雙手|1F64C-1F3FB:🙌🏻 1F64C-1F3FC:🙌🏼 1F64C-1F3FD:🙌🏽 1F64C-1F3FE:🙌🏾 1F64C-1F3FF:🙌🏿
1|1FAF6|🫶|heart hands|雙手心形|<3 hands heart love you heart hands heart_hands 雙手心形 <3 愛 愛你 愛心 手|1FAF6-1F3FB:🫶🏻 1FAF6-1F3FC:🫶🏼 1FAF6-1F3FD:🫶🏽 1FAF6-1F3FE:🫶🏾 1FAF6-1F3FF:🫶🏿
1|1F450|👐|open hands|攤開手|hand hands hug jazz open swerve open hands open_hands 攤開手 手 雙手|1F450-1F3FB:👐🏻 1F450-1F3FC:👐🏼 1F450-1F3FD:👐🏽 1F450-1F3FE:👐🏾 1F450-1F3FF:👐🏿
1|1F932|🤲|palms up together|雙手掌朝上|cupped dua hands palms pray prayer together up wish palms up together palms_up_together 雙手掌朝上 合掌向上 祈禱 禱告|1F932-1F3FB:🤲🏻 1F932-1F3FC:🤲🏼 1F932-1F3FD:🤲🏽 1F932-1F3FE:🤲🏾 1F932-1F3FF:🤲🏿
1|1F91D|🤝|handshake|握手|agreement deal hand meeting shake handshake 握手 成交 手 握 講定|1F91D-1F3FB:🤝🏻 1F91D-1F3FC:🤝🏼 1F91D-1F3FD:🤝🏽 1F91D-1F3FE:🤝🏾 1F91D-1F3FF:🤝🏿 1FAF1-1F3FB-200D-1FAF2-1F3FC:🫱🏻‍🫲🏼 1FAF1-1F3FB-200D-1FAF2-1F3FD:🫱🏻‍🫲🏽 1FAF1-1F3FB-200D-1FAF2-1F3FE:🫱🏻‍🫲🏾 1FAF1-1F3FB-200D-1FAF2-1F3FF:🫱🏻‍🫲🏿 1FAF1-1F3FC-200D-1FAF2-1F3FB:🫱🏼‍🫲🏻 1FAF1-1F3FC-200D-1FAF2-1F3FD:🫱🏼‍🫲🏽 1FAF1-1F3FC-200D-1FAF2-1F3FE:🫱🏼‍🫲🏾 1FAF1-1F3FC-200D-1FAF2-1F3FF:🫱🏼‍🫲🏿 1FAF1-1F3FD-200D-1FAF2-1F3FB:🫱🏽‍🫲🏻 1FAF1-1F3FD-200D-1FAF2-1F3FC:🫱🏽‍🫲🏼 1FAF1-1F3FD-200D-1FAF2-1F3FE:🫱🏽‍🫲🏾 1FAF1-1F3FD-200D-1FAF2-1F3FF:🫱🏽‍🫲🏿 1FAF1-1F3FE-200D-1FAF2-1F3FB:🫱🏾‍🫲🏻 1FAF1-1F3FE-200D-1FAF2-1F3FC:🫱🏾‍🫲🏼 1FAF1-1F3FE-200D-1FAF2-1F3FD:🫱🏾‍🫲🏽 1FAF1-1F3FE-200D-1FAF2-1F3FF:🫱🏾‍🫲🏿 1FAF1-1F3FF-200D-1FAF2-1F3FB:🫱🏿‍🫲🏻 1FAF1-1F3FF-200D-1FAF2-1F3FC:🫱🏿‍🫲🏼 1FAF1-1F3FF-200D-1FAF2-1F3FD:🫱🏿‍🫲🏽 1FAF1-1F3FF-200D-1FAF2-1F3FE:🫱🏿‍🫲🏾
1|1F64F|🙏|folded hands|感恩|appreciate ask beg blessed bow cmon five folded gesture hand high please pray thanks thx folded hands 感恩 感激 謝謝 阿彌陀佛 雙手合一|1F64F-1F3FB:🙏🏻 1F64F-1F3FC:🙏🏼 1F64F-1F3FD:🙏🏽 1F64F-1F3FE:🙏🏾 1F64F-1F3FF:🙏🏿
1|270D|✍️|writing hand|寫|hand write writing writing hand writing_hand 寫 手寫 書寫 記錄|270D-1F3FB:✍🏻 270D-1F3FC:✍🏼 270D-1F3FD:✍🏽 270D-1F3FE:✍🏾 270D-1F3FF:✍🏿
1|1F485|💅|nail polish|指甲油|bored care cosmetics done makeup manicure nail polish whatever nail polish nail_care 指甲油 修指甲 沒事了 無聊 美甲|1F485-1F3FB:💅🏻 1F485-1F3FC:💅🏼 1F485-1F3FD:💅🏽 1F485-1F3FE:💅🏾 1F485-1F3FF:💅🏿
1|1F933|🤳|selfie|自拍|camera phone selfie 自拍 拍 相機 自|1F933-1F3FB:🤳🏻 1F933-1F3FC:🤳🏼 1F933-1F3FD:🤳🏽 1F933-1F3FE:🤳🏾 1F933-1F3FF:🤳🏿
1|1F4AA|💪|flexed biceps|肌肉|arm beast bench biceps bodybuilder bro curls flex gains gym jacked muscle press ripped strong weightlift flexed biceps 肌肉 二頭肌 強壯|1F4AA-1F3FB:💪🏻 1F4AA-1F3FC:💪🏼 1F4AA-1F3FD:💪🏽 1F4AA-1F3FE:💪🏾 1F4AA-1F3FF:💪🏿
1|1F9BE|🦾|mechanical arm|機械手臂|accessibility arm mechanical prosthetic mechanical arm mechanical_arm 機械手臂 義肢 行動不便|
1|1F9BF|🦿|mechanical leg|機械腳|accessibility leg mechanical prosthetic mechanical leg mechanical_leg 機械腳 義肢 行動不便|
1|1F9B5|🦵|leg|腿|bent foot kick knee limb leg 腿 四肢 曲腿 腳 膝蓋 踢|1F9B5-1F3FB:🦵🏻 1F9B5-1F3FC:🦵🏼 1F9B5-1F3FD:🦵🏽 1F9B5-1F3FE:🦵🏾 1F9B5-1F3FF:🦵🏿
1|1F9B6|🦶|foot|腳|ankle feet kick stomp foot 腳 腳踝 跺腳 踢 踩|1F9B6-1F3FB:🦶🏻 1F9B6-1F3FC:🦶🏼 1F9B6-1F3FD:🦶🏽 1F9B6-1F3FE:🦶🏾 1F9B6-1F3FF:🦶🏿
1|1F442|👂️|ear|耳朵|body ears hear hearing listen listening sound ear 耳朵 傾聽 聽 身體部位|1F442-1F3FB:👂🏻 1F442-1F3FC:👂🏼 1F442-1F3FD:👂🏽 1F442-1F3FE:👂🏾 1F442-1F3FF:👂🏿
1|1F9BB|🦻|ear with hearing aid|戴助聽器的耳朵|accessibility aid ear hard hearing ear with hearing aid ear_with_hearing_aid 戴助聽器的耳朵 耳聾 聽障 行動不便|1F9BB-1F3FB:🦻🏻 1F9BB-1F3FC:🦻🏼 1F9BB-1F3FD:🦻🏽 1F9BB-1F3FE:🦻🏾 1F9BB-1F3FF:🦻🏿
1|1F443|👃|nose|鼻子|body noses nosey odor smell smells nose 鼻子 味道 氣味 聞到 身體部位|1F443-1F3FB:👃🏻 1F443-1F3FC:👃🏼 1F443-1F3FD:👃🏽 1F443-1F3FE:👃🏾 1F443-1F3FF:👃🏿
1|1F9E0|🧠|brain|腦|intelligent smart brain 腦 大腦 聰明 腦袋|
1|1FAC0|🫀|anatomical heart|心臟|anatomical beat cardiology heart heartbeat organ pulse real red anatomical heart anatomical_heart 心臟 器官 心 心跳 紅色 脈搏|
1|1FAC1|🫁|lungs|肺|breath breathe exhalation inhalation lung organ respiration lungs 肺 呼吸 器官|
1|1F9B7|🦷|tooth|牙齒|dentist pearly teeth white tooth 牙齒 牙醫 珍珠色 白色|
1|1F9B4|🦴|bone|骨頭|bones dog skeleton wishbone bone 骨頭 如願骨 狗 骨架|
1|1F440|👀|eyes|雙眼|body eye face googly look looking omg peep see seeing eyes 雙眼 兩隻大眼睛 看 窺視 身體部位|
1|1F441|👁️|eye|眼睛|1 body one eye 眼睛 單眼 身體部位|
1|1F445|👅|tongue|舌頭|body lick slurp tongue 舌頭 吸吮 舔 身體部位|
1|1F444|👄|mouth|嘴巴|beauty body kiss kissing lips lipstick mouth 嘴巴 口紅 嘴唇 親吻 身體部位|
1|1FAE6|🫦|biting lip|咬唇|anxious bite biting fear flirt flirting kiss lip lipstick nervous sexy uncomfortable worried worry biting lip biting_lip 咬唇 不舒服 吻 嘴唇 害怕 性感 憂慮 擔憂 焦慮 緊張|
1|1F476|👶|baby|小寶寶|babies children goo infant newborn pregnant young baby 小寶寶 初生兒 小嬰兒 懷孕 臉|1F476-1F3FB:👶🏻 1F476-1F3FC:👶🏼 1F476-1F3FD:👶🏽 1F476-1F3FE:👶🏾 1F476-1F3FF:👶🏿
1|1F9D2|🧒|child|小孩|bright-eyed grandchild kid young younger child 小孩 兒童 孩子|1F9D2-1F3FB:🧒🏻 1F9D2-1F3FC:🧒🏼 1F9D2-1F3FD:🧒🏽 1F9D2-1F3FE:🧒🏾 1F9D2-1F3FF:🧒🏿
1|1F466|👦|boy|男孩|bright-eyed child grandson kid son young younger boy 男孩 男孩|1F466-1F3FB:👦🏻 1F466-1F3FC:👦🏼 1F466-1F3FD:👦🏽 1F466-1F3FE:👦🏾 1F466-1F3FF:👦🏿
1|1F467|👧|girl|女孩|bright-eyed child daughter granddaughter kid virgo young younger zodiac girl 女孩 眼睛明亮 眼睛發光 辮子|1F467-1F3FB:👧🏻 1F467-1F3FC:👧🏼 1F467-1F3FD:👧🏽 1F467-1F3FE:👧🏾 1F467-1F3FF:👧🏿
1|1F9D1|🧑|person|大人|adult person 大人 成人|1F9D1-1F3FB:🧑🏻 1F9D1-1F3FC:🧑🏼 1F9D1-1F3FD:🧑🏽 1F9D1-1F3FE:🧑🏾 1F9D1-1F3FF:🧑🏿
1|1F471|👱|person: blond hair|金髮人|blond blond-haired human person person: blond hair blond_haired_person 金髮人 人物 金髮|1F471-1F3FB:👱🏻 1F471-1F3FC:👱🏼 1F471-1F3FD:👱🏽 1F471-1F3FE:👱🏾 1F471-1F3FF:👱🏿
1|1F468|👨|man|男人|adult bro man 男人 兄弟 男 男性 男朋友|1F468-1F3FB:👨🏻 1F468-1F3FC:👨🏼 1F468-1F3FD:👨🏽 1F468-1F3FE:👨🏾 1F468-1F3FF:👨🏿
1|1F9D4|🧔|person: beard|蓄鬍的人|beard bearded person whiskers person: beard bearded_person 蓄鬍的人 大鬍男 鬍子 鬍鬚|1F9D4-1F3FB:🧔🏻 1F9D4-1F3FC:🧔🏼 1F9D4-1F3FD:🧔🏽 1F9D4-1F3FE:🧔🏾 1F9D4-1F3FF:🧔🏿
1|1F9D4-200D-2642-FE0F|🧔‍♂️|man: beard|男人: 蓄鬍的人|beard bearded man whiskers man: beard man_beard 男人: 蓄鬍的人 男人 鬍子|1F9D4-1F3FB-200D-2642-FE0F:🧔🏻‍♂️ 1F9D4-1F3FC-200D-2642-FE0F:🧔🏼‍♂️ 1F9D4-1F3FD-200D-2642-FE0F:🧔🏽‍♂️ 1F9D4-1F3FE-200D-2642-FE0F:🧔🏾‍♂️ 1F9D4-1F3FF-200D-2642-FE0F:🧔🏿‍♂️
1|1F9D4-200D-2640-FE0F|🧔‍♀️|woman: beard|女人: 蓄鬍的人|beard bearded whiskers woman woman: beard woman_beard 女人: 蓄鬍的人 女人 鬍子|1F9D4-1F3FB-200D-2640-FE0F:🧔🏻‍♀️ 1F9D4-1F3FC-200D-2640-FE0F:🧔🏼‍♀️ 1F9D4-1F3FD-200D-2640-FE0F:🧔🏽‍♀️ 1F9D4-1F3FE-200D-2640-FE0F:🧔🏾‍♀️ 1F9D4-1F3FF-200D-2640-FE0F:🧔🏿‍♀️
1|1F468-200D-1F9B0|👨‍🦰|man: red hair|男人：紅髮|adult bro man red hair man: red hair red_haired_man 男人：紅髮 兄弟 男 男人 男性 男朋友 紅髮|1F468-1F3FB-200D-1F9B0:👨🏻‍🦰 1F468-1F3FC-200D-1F9B0:👨🏼‍🦰 1F468-1F3FD-200D-1F9B0:👨🏽‍🦰 1F468-1F3FE-200D-1F9B0:👨🏾‍🦰 1F468-1F3FF-200D-1F9B0:👨🏿‍🦰
1|1F468-200D-1F9B1|👨‍🦱|man: curly hair|男人：卷髮|adult bro curly hair man man: curly hair curly_haired_man 男人：卷髮 兄弟 卷髮 男 男人 男性 男朋友|1F468-1F3FB-200D-1F9B1:👨🏻‍🦱 1F468-1F3FC-200D-1F9B1:👨🏼‍🦱 1F468-1F3FD-200D-1F9B1:👨🏽‍🦱 1F468-1F3FE-200D-1F9B1:👨🏾‍🦱 1F468-1F3FF-200D-1F9B1:👨🏿‍🦱
1|1F468-200D-1F9B3|👨‍🦳|man: white hair|男人：白髮|adult bro man white hair man: white hair white_haired_man 男人：白髮 兄弟 男 男人 男性 男朋友 白髮|1F468-1F3FB-200D-1F9B3:👨🏻‍🦳 1F468-1F3FC-200D-1F9B3:👨🏼‍🦳 1F468-1F3FD-200D-1F9B3:👨🏽‍🦳 1F468-1F3FE-200D-1F9B3:👨🏾‍🦳 1F468-1F3FF-200D-1F9B3:👨🏿‍🦳
1|1F468-200D-1F9B2|👨‍🦲|man: bald|男人：禿頭|adult bald bro man man: bald bald_man 男人：禿頭 兄弟 男 男人 男性 男朋友 禿頭|1F468-1F3FB-200D-1F9B2:👨🏻‍🦲 1F468-1F3FC-200D-1F9B2:👨🏼‍🦲 1F468-1F3FD-200D-1F9B2:👨🏽‍🦲 1F468-1F3FE-200D-1F9B2:👨🏾‍🦲 1F468-1F3FF-200D-1F9B2:👨🏿‍🦲
1|1F469|👩|woman|女人|adult lady woman 女人 女 女性|1F469-1F3FB:👩🏻 1F469-1F3FC:👩🏼 1F469-1F3FD:👩🏽 1F469-1F3FE:👩🏾 1F469-1F3FF:👩🏿
1|1F469-200D-1F9B0|👩‍🦰|woman: red hair|女人：紅髮|adult lady red hair woman woman: red hair red_haired_woman 女人：紅髮 女 女人 女性 紅髮|1F469-1F3FB-200D-1F9B0:👩🏻‍🦰 1F469-1F3FC-200D-1F9B0:👩🏼‍🦰 1F469-1F3FD-200D-1F9B0:👩🏽‍🦰 1F469-1F3FE-200D-1F9B0:👩🏾‍🦰 1F469-1F3FF-200D-1F9B0:👩🏿‍🦰
1|1F9D1-200D-1F9B0|🧑‍🦰|person: red hair|大人：紅髮|adult person red hair person: red hair person_red_hair 大人：紅髮 大人 成人 紅髮|1F9D1-1F3FB-200D-1F9B0:🧑🏻‍🦰 1F9D1-1F3FC-200D-1F9B0:🧑🏼‍🦰 1F9D1-1F3FD-200D-1F9B0:🧑🏽‍🦰 1F9D1-1F3FE-200D-1F9B0:🧑🏾‍🦰 1F9D1-1F3FF-200D-1F9B0:🧑🏿‍🦰
1|1F469-200D-1F9B1|👩‍🦱|woman: curly hair|女人：卷髮|adult curly hair lady woman woman: curly hair curly_haired_woman 女人：卷髮 卷髮 女 女人 女性|1F469-1F3FB-200D-1F9B1:👩🏻‍🦱 1F469-1F3FC-200D-1F9B1:👩🏼‍🦱 1F469-1F3FD-200D-1F9B1:👩🏽‍🦱 1F469-1F3FE-200D-1F9B1:👩🏾‍🦱 1F469-1F3FF-200D-1F9B1:👩🏿‍🦱
1|1F9D1-200D-1F9B1|🧑‍🦱|person: curly hair|大人：卷髮|adult curly hair person person: curly hair person_curly_hair 大人：卷髮 卷髮 大人 成人|1F9D1-1F3FB-200D-1F9B1:🧑🏻‍🦱 1F9D1-1F3FC-200D-1F9B1:🧑🏼‍🦱 1F9D1-1F3FD-200D-1F9B1:🧑🏽‍🦱 1F9D1-1F3FE-200D-1F9B1:🧑🏾‍🦱 1F9D1-1F3FF-200D-1F9B1:🧑🏿‍🦱
1|1F469-200D-1F9B3|👩‍🦳|woman: white hair|女人：白髮|adult lady white hair woman woman: white hair white_haired_woman 女人：白髮 女 女人 女性 白髮|1F469-1F3FB-200D-1F9B3:👩🏻‍🦳 1F469-1F3FC-200D-1F9B3:👩🏼‍🦳 1F469-1F3FD-200D-1F9B3:👩🏽‍🦳 1F469-1F3FE-200D-1F9B3:👩🏾‍🦳 1F469-1F3FF-200D-1F9B3:👩🏿‍🦳
1|1F9D1-200D-1F9B3|🧑‍🦳|person: white hair|大人：白髮|adult person white hair person: white hair person_white_hair 大人：白髮 大人 成人 白髮|1F9D1-1F3FB-200D-1F9B3:🧑🏻‍🦳 1F9D1-1F3FC-200D-1F9B3:🧑🏼‍🦳 1F9D1-1F3FD-200D-1F9B3:🧑🏽‍🦳 1F9D1-1F3FE-200D-1F9B3:🧑🏾‍🦳 1F9D1-1F3FF-200D-1F9B3:🧑🏿‍🦳
1|1F469-200D-1F9B2|👩‍🦲|woman: bald|女人：禿頭|adult bald lady woman woman: bald bald_woman 女人：禿頭 女 女人 女性 禿頭|1F469-1F3FB-200D-1F9B2:👩🏻‍🦲 1F469-1F3FC-200D-1F9B2:👩🏼‍🦲 1F469-1F3FD-200D-1F9B2:👩🏽‍🦲 1F469-1F3FE-200D-1F9B2:👩🏾‍🦲 1F469-1F3FF-200D-1F9B2:👩🏿‍🦲
1|1F9D1-200D-1F9B2|🧑‍🦲|person: bald|大人：禿頭|adult bald person person: bald person_bald 大人：禿頭 大人 成人 禿頭|1F9D1-1F3FB-200D-1F9B2:🧑🏻‍🦲 1F9D1-1F3FC-200D-1F9B2:🧑🏼‍🦲 1F9D1-1F3FD-200D-1F9B2:🧑🏽‍🦲 1F9D1-1F3FE-200D-1F9B2:🧑🏾‍🦲 1F9D1-1F3FF-200D-1F9B2:🧑🏿‍🦲
1|1F471-200D-2640-FE0F|👱‍♀️|woman: blond hair|金髮女|blond blond-haired blonde hair woman woman: blond hair blond_haired_woman blonde_woman 金髮女 女 金髮|1F471-1F3FB-200D-2640-FE0F:👱🏻‍♀️ 1F471-1F3FC-200D-2640-FE0F:👱🏼‍♀️ 1F471-1F3FD-200D-2640-FE0F:👱🏽‍♀️ 1F471-1F3FE-200D-2640-FE0F:👱🏾‍♀️ 1F471-1F3FF-200D-2640-FE0F:👱🏿‍♀️
1|1F471-200D-2642-FE0F|👱‍♂️|man: blond hair|金髮男|blond blond-haired hair man man: blond hair blond_haired_man 金髮男 男 金髮|1F471-1F3FB-200D-2642-FE0F:👱🏻‍♂️ 1F471-1F3FC-200D-2642-FE0F:👱🏼‍♂️ 1F471-1F3FD-200D-2642-FE0F:👱🏽‍♂️ 1F471-1F3FE-200D-2642-FE0F:👱🏾‍♂️ 1F471-1F3FF-200D-2642-FE0F:👱🏿‍♂️
1|1F9D3|🧓|older person|長者|adult elderly grandparent old person wise older person older_adult 長者 老 老人 老男人|1F9D3-1F3FB:🧓🏻 1F9D3-1F3FC:🧓🏼 1F9D3-1F3FD:🧓🏽 1F9D3-1F3FE:🧓🏾 1F9D3-1F3FF:🧓🏿
1|1F474|👴|old man|老爺爺|adult bald elderly gramps grandfather grandpa man old wise old man older_man 老爺爺 有智慧 祖父 老頭 臉|1F474-1F3FB:👴🏻 1F474-1F3FC:👴🏼 1F474-1F3FD:👴🏽 1F474-1F3FE:👴🏾 1F474-1F3FF:👴🏿
1|1F475|👵|old woman|老奶奶|adult elderly grandma grandmother granny lady old wise woman old woman older_woman 老奶奶 祖母 老太太|1F475-1F3FB:👵🏻 1F475-1F3FC:👵🏼 1F475-1F3FD:👵🏽 1F475-1F3FE:👵🏾 1F475-1F3FF:👵🏿
1|1F64D|🙍|person frowning|皺眉|annoyed disappointed disgruntled disturbed frown frowning frustrated gesture irritated person upset person frowning frowning_person 皺眉 不爽 不高興 人物 眉頭深鎖 表情|1F64D-1F3FB:🙍🏻 1F64D-1F3FC:🙍🏼 1F64D-1F3FD:🙍🏽 1F64D-1F3FE:🙍🏾 1F64D-1F3FF:🙍🏿
1|1F64D-200D-2642-FE0F|🙍‍♂️|man frowning|男生皺眉|annoyed disappointed disgruntled disturbed frown frowning frustrated gesture irritated man upset man frowning frowning_man 男生皺眉 不爽 男 皺眉 表情|1F64D-1F3FB-200D-2642-FE0F:🙍🏻‍♂️ 1F64D-1F3FC-200D-2642-FE0F:🙍🏼‍♂️ 1F64D-1F3FD-200D-2642-FE0F:🙍🏽‍♂️ 1F64D-1F3FE-200D-2642-FE0F:🙍🏾‍♂️ 1F64D-1F3FF-200D-2642-FE0F:🙍🏿‍♂️
1|1F64D-200D-2640-FE0F|🙍‍♀️|woman frowning|女生皺眉|annoyed disappointed disgruntled disturbed frown frowning frustrated gesture irritated upset woman woman frowning frowning_woman 女生皺眉 女 皺眉 表情|1F64D-1F3FB-200D-2640-FE0F:🙍🏻‍♀️ 1F64D-1F3FC-200D-2640-FE0F:🙍🏼‍♀️ 1F64D-1F3FD-200D-2640-FE0F:🙍🏽‍♀️ 1F64D-1F3FE-200D-2640-FE0F:🙍🏾‍♀️ 1F64D-1F3FF-200D-2640-FE0F:🙍🏿‍♀️
1|1F64E|🙎|person pouting|生氣的人|disappointed downtrodden frown grimace person pouting scowl sulk upset whine person pouting pouting_face 生氣的人 不爽 不高興 人物 噘嘴 噘嘴的女人 生氣 皺眉 表情|1F64E-1F3FB:🙎🏻 1F64E-1F3FC:🙎🏼 1F64E-1F3FD:🙎🏽 1F64E-1F3FE:🙎🏾 1F64E-1F3FF:🙎🏿
1|1F64E-200D-2642-FE0F|🙎‍♂️|man pouting|男生噘嘴|disappointed downtrodden frown grimace man pouting scowl sulk upset whine man pouting pouting_man 男生噘嘴 噘嘴 生氣|1F64E-1F3FB-200D-2642-FE0F:🙎🏻‍♂️ 1F64E-1F3FC-200D-2642-FE0F:🙎🏼‍♂️ 1F64E-1F3FD-200D-2642-FE0F:🙎🏽‍♂️ 1F64E-1F3FE-200D-2642-FE0F:🙎🏾‍♂️ 1F64E-1F3FF-200D-2642-FE0F:🙎🏿‍♂️
1|1F64E-200D-2640-FE0F|🙎‍♀️|woman pouting|女生噘嘴|disappointed downtrodden frown grimace pouting scowl sulk upset whine woman woman pouting pouting_woman 女生噘嘴 噘嘴 生氣|1F64E-1F3FB-200D-2640-FE0F:🙎🏻‍♀️ 1F64E-1F3FC-200D-2640-FE0F:🙎🏼‍♀️ 1F64E-1F3FD-200D-2640-FE0F:🙎🏽‍♀️ 1F64E-1F3FE-200D-2640-FE0F:🙎🏾‍♀️ 1F64E-1F3FF-200D-2640-FE0F:🙎🏿‍♀️
1|1F645|🙅|person gesturing NO|不行|forbidden gesture hand no not person prohibit person gesturing no no_good 不行 ng 不正確 人物 禁止 答錯 錯誤|1F645-1F3FB:🙅🏻 1F645-1F3FC:🙅🏼 1F645-1F3FD:🙅🏽 1F645-1F3FE:🙅🏾 1F645-1F3FF:🙅🏿
1|1F645-200D-2642-FE0F|🙅‍♂️|man gesturing NO|男生手比叉|forbidden gesture hand man no not prohibit man gesturing no ng_man no_good_man 男生手比叉 ng 不行 叉 打叉 禁止|1F645-1F3FB-200D-2642-FE0F:🙅🏻‍♂️ 1F645-1F3FC-200D-2642-FE0F:🙅🏼‍♂️ 1F645-1F3FD-200D-2642-FE0F:🙅🏽‍♂️ 1F645-1F3FE-200D-2642-FE0F:🙅🏾‍♂️ 1F645-1F3FF-200D-2642-FE0F:🙅🏿‍♂️
1|1F645-200D-2640-FE0F|🙅‍♀️|woman gesturing NO|女生手比叉|forbidden gesture hand no not prohibit woman woman gesturing no ng_woman no_good_woman 女生手比叉 ng 不行 叉 禁止|1F645-1F3FB-200D-2640-FE0F:🙅🏻‍♀️ 1F645-1F3FC-200D-2640-FE0F:🙅🏼‍♀️ 1F645-1F3FD-200D-2640-FE0F:🙅🏽‍♀️ 1F645-1F3FE-200D-2640-FE0F:🙅🏾‍♀️ 1F645-1F3FF-200D-2640-FE0F:🙅🏿‍♀️
1|1F646|🙆|person gesturing OK|可以|exercise gesture gesturing hand ok omg person person gesturing ok ok_person 可以 ok omg 人物 圈 我的天 正確 答對 運動|1F646-1F3FB:🙆🏻 1F646-1F3FC:🙆🏼 1F646-1F3FD:🙆🏽 1F646-1F3FE:🙆🏾 1F646-1F3FF:🙆🏿
1|1F646-200D-2642-FE0F|🙆‍♂️|man gesturing OK|男生手比圈|exercise gesture gesturing hand man ok omg man gesturing ok ok_man 男生手比圈 ok 可以 圈 好 我的天|1F646-1F3FB-200D-2642-FE0F:🙆🏻‍♂️ 1F646-1F3FC-200D-2642-FE0F:🙆🏼‍♂️ 1F646-1F3FD-200D-2642-FE0F:🙆🏽‍♂️ 1F646-1F3FE-200D-2642-FE0F:🙆🏾‍♂️ 1F646-1F3FF-200D-2642-FE0F:🙆🏿‍♂️
1|1F646-200D-2640-FE0F|🙆‍♀️|woman gesturing OK|女生手比圈|exercise gesture gesturing hand ok omg woman woman gesturing ok ok_woman 女生手比圈 ok 可以 圈|1F646-1F3FB-200D-2640-FE0F:🙆🏻‍♀️ 1F646-1F3FC-200D-2640-FE0F:🙆🏼‍♀️ 1F646-1F3FD-200D-2640-FE0F:🙆🏽‍♀️ 1F646-1F3FE-200D-2640-FE0F:🙆🏾‍♀️ 1F646-1F3FF-200D-2640-FE0F:🙆🏿‍♀️
1|1F481|💁|person tipping hand|服務台人員|fetch flick flip gossip hand person sarcasm sarcastic sassy seriously tipping whatever person tipping hand information_desk_person tipping_hand_person 服務台人員 人物 俏皮 分髮 嘲諷 服務員 給小費 髮夾|1F481-1F3FB:💁🏻 1F481-1F3FC:💁🏼 1F481-1F3FD:💁🏽 1F481-1F3FE:💁🏾 1F481-1F3FF:💁🏿
1|1F481-200D-2642-FE0F|💁‍♂️|man tipping hand|男生抬手|fetch flick flip gossip hand man sarcasm sarcastic sassy seriously tipping whatever man tipping hand sassy_man tipping_hand_man 男生抬手 嘲諷 小費 男 調皮 隨便|1F481-1F3FB-200D-2642-FE0F:💁🏻‍♂️ 1F481-1F3FC-200D-2642-FE0F:💁🏼‍♂️ 1F481-1F3FD-200D-2642-FE0F:💁🏽‍♂️ 1F481-1F3FE-200D-2642-FE0F:💁🏾‍♂️ 1F481-1F3FF-200D-2642-FE0F:💁🏿‍♂️
1|1F481-200D-2640-FE0F|💁‍♀️|woman tipping hand|女生抬手|fetch flick flip gossip hand sarcasm sarcastic sassy seriously tipping whatever woman woman tipping hand sassy_woman tipping_hand_woman 女生抬手 女 小費|1F481-1F3FB-200D-2640-FE0F:💁🏻‍♀️ 1F481-1F3FC-200D-2640-FE0F:💁🏼‍♀️ 1F481-1F3FD-200D-2640-FE0F:💁🏽‍♀️ 1F481-1F3FE-200D-2640-FE0F:💁🏾‍♀️ 1F481-1F3FF-200D-2640-FE0F:💁🏿‍♀️
1|1F64B|🙋|person raising hand|嗨|gesture hand here know me person pick question raise raising person raising hand raising_hand 嗨 +1 你好 打招呼 舉手 舉手的人 舉手的女人 贊成 這裡 選我|1F64B-1F3FB:🙋🏻 1F64B-1F3FC:🙋🏼 1F64B-1F3FD:🙋🏽 1F64B-1F3FE:🙋🏾 1F64B-1F3FF:🙋🏿
1|1F64B-200D-2642-FE0F|🙋‍♂️|man raising hand|男生舉手|gesture hand here know man me pick question raise raising man raising hand raising_hand_man 男生舉手 作手勢 嗨 我知道 打招呼 有問題 男 舉手|1F64B-1F3FB-200D-2642-FE0F:🙋🏻‍♂️ 1F64B-1F3FC-200D-2642-FE0F:🙋🏼‍♂️ 1F64B-1F3FD-200D-2642-FE0F:🙋🏽‍♂️ 1F64B-1F3FE-200D-2642-FE0F:🙋🏾‍♂️ 1F64B-1F3FF-200D-2642-FE0F:🙋🏿‍♂️
1|1F64B-200D-2640-FE0F|🙋‍♀️|woman raising hand|女生舉手|gesture hand here know me pick question raise raising woman woman raising hand raising_hand_woman 女生舉手 嗨 女 打招呼 舉手|1F64B-1F3FB-200D-2640-FE0F:🙋🏻‍♀️ 1F64B-1F3FC-200D-2640-FE0F:🙋🏼‍♀️ 1F64B-1F3FD-200D-2640-FE0F:🙋🏽‍♀️ 1F64B-1F3FE-200D-2640-FE0F:🙋🏾‍♀️ 1F64B-1F3FF-200D-2640-FE0F:🙋🏿‍♀️
1|1F9CF|🧏|deaf person|聽障人士|accessibility deaf ear gesture hear person deaf person deaf_person 聽障人士 耳朵 耳聾 聽不見 聽力|1F9CF-1F3FB:🧏🏻 1F9CF-1F3FC:🧏🏼 1F9CF-1F3FD:🧏🏽 1F9CF-1F3FE:🧏🏾 1F9CF-1F3FF:🧏🏿
1|1F9CF-200D-2642-FE0F|🧏‍♂️|deaf man|聽障男子|accessibility deaf ear gesture hear man deaf man deaf_man 聽障男子 男 聽障|1F9CF-1F3FB-200D-2642-FE0F:🧏🏻‍♂️ 1F9CF-1F3FC-200D-2642-FE0F:🧏🏼‍♂️ 1F9CF-1F3FD-200D-2642-FE0F:🧏🏽‍♂️ 1F9CF-1F3FE-200D-2642-FE0F:🧏🏾‍♂️ 1F9CF-1F3FF-200D-2642-FE0F:🧏🏿‍♂️
1|1F9CF-200D-2640-FE0F|🧏‍♀️|deaf woman|聽障女子|accessibility deaf ear gesture hear woman deaf woman deaf_woman 聽障女子 女 聽障|1F9CF-1F3FB-200D-2640-FE0F:🧏🏻‍♀️ 1F9CF-1F3FC-200D-2640-FE0F:🧏🏼‍♀️ 1F9CF-1F3FD-200D-2640-FE0F:🧏🏽‍♀️ 1F9CF-1F3FE-200D-2640-FE0F:🧏🏾‍♀️ 1F9CF-1F3FF-200D-2640-FE0F:🧏🏿‍♀️
1|1F647|🙇|person bowing|鞠躬|apology ask beg bow bowing favor forgive gesture meditate meditation person pity regret sorry person bowing 鞠躬 下跪 不好意思 原諒 姿勢 姿態 對不起 後悔 抱歉 道歉|1F647-1F3FB:🙇🏻 1F647-1F3FC:🙇🏼 1F647-1F3FD:🙇🏽 1F647-1F3FE:🙇🏾 1F647-1F3FF:🙇🏿
1|1F647-200D-2642-FE0F|🙇‍♂️|man bowing|男生鞠躬|apology ask beg bow bowing favor forgive gesture man meditate meditation pity regret sorry man bowing bowing_man 男生鞠躬 不好意思 男 道歉|1F647-1F3FB-200D-2642-FE0F:🙇🏻‍♂️ 1F647-1F3FC-200D-2642-FE0F:🙇🏼‍♂️ 1F647-1F3FD-200D-2642-FE0F:🙇🏽‍♂️ 1F647-1F3FE-200D-2642-FE0F:🙇🏾‍♂️ 1F647-1F3FF-200D-2642-FE0F:🙇🏿‍♂️
1|1F647-200D-2640-FE0F|🙇‍♀️|woman bowing|女生鞠躬|apology ask beg bow bowing favor forgive gesture meditate meditation pity regret sorry woman woman bowing bowing_woman 女生鞠躬 不好意思 女 沈思 道歉 靜思|1F647-1F3FB-200D-2640-FE0F:🙇🏻‍♀️ 1F647-1F3FC-200D-2640-FE0F:🙇🏼‍♀️ 1F647-1F3FD-200D-2640-FE0F:🙇🏽‍♀️ 1F647-1F3FE-200D-2640-FE0F:🙇🏾‍♀️ 1F647-1F3FF-200D-2640-FE0F:🙇🏿‍♀️
1|1F926|🤦|person facepalming|哦不|again bewilder disbelief exasperation facepalm no not oh omg person shock smh person facepalming 哦不 不敢相信 喔不 噢不 天啊 怎麼會 捂臉 無言 難以置信 震驚|1F926-1F3FB:🤦🏻 1F926-1F3FC:🤦🏼 1F926-1F3FD:🤦🏽 1F926-1F3FE:🤦🏾 1F926-1F3FF:🤦🏿
1|1F926-200D-2642-FE0F|🤦‍♂️|man facepalming|男生遮臉|again bewilder disbelief exasperation facepalm man no not oh omg shock smh man facepalming man_facepalming 男生遮臉 不敢相信 喔不 噢不 天啊 完了 拜託不要 捂臉 無言 難以置信|1F926-1F3FB-200D-2642-FE0F:🤦🏻‍♂️ 1F926-1F3FC-200D-2642-FE0F:🤦🏼‍♂️ 1F926-1F3FD-200D-2642-FE0F:🤦🏽‍♂️ 1F926-1F3FE-200D-2642-FE0F:🤦🏾‍♂️ 1F926-1F3FF-200D-2642-FE0F:🤦🏿‍♂️
1|1F926-200D-2640-FE0F|🤦‍♀️|woman facepalming|女生遮臉|again bewilder disbelief exasperation facepalm no not oh omg shock smh woman woman facepalming woman_facepalming 女生遮臉 不敢相信 喔不 噢不 天啊 女 捂臉 無言 難以置信 震驚|1F926-1F3FB-200D-2640-FE0F:🤦🏻‍♀️ 1F926-1F3FC-200D-2640-FE0F:🤦🏼‍♀️ 1F926-1F3FD-200D-2640-FE0F:🤦🏽‍♀️ 1F926-1F3FE-200D-2640-FE0F:🤦🏾‍♀️ 1F926-1F3FF-200D-2640-FE0F:🤦🏿‍♀️
1|1F937|🤷|person shrugging|聳肩|doubt dunno guess idk ignorance indifference knows maybe person shrug shrugging whatever who person shrugging 聳肩 不知道 不關心 懷疑 我猜 攤手 聳 肩 說不定 隨便|1F937-1F3FB:🤷🏻 1F937-1F3FC:🤷🏼 1F937-1F3FD:🤷🏽 1F937-1F3FE:🤷🏾 1F937-1F3FF:🤷🏿
1|1F937-200D-2642-FE0F|🤷‍♂️|man shrugging|男生聳肩|doubt dunno guess idk ignorance indifference knows man maybe shrug shrugging whatever who man shrugging man_shrugging 男生聳肩 不在乎 不知道 可能 大概 應該 我猜 男 聳肩 都好 隨便|1F937-1F3FB-200D-2642-FE0F:🤷🏻‍♂️ 1F937-1F3FC-200D-2642-FE0F:🤷🏼‍♂️ 1F937-1F3FD-200D-2642-FE0F:🤷🏽‍♂️ 1F937-1F3FE-200D-2642-FE0F:🤷🏾‍♂️ 1F937-1F3FF-200D-2642-FE0F:🤷🏿‍♂️
1|1F937-200D-2640-FE0F|🤷‍♀️|woman shrugging|女生聳肩|doubt dunno guess idk ignorance indifference knows maybe shrug shrugging whatever who woman woman shrugging woman_shrugging 女生聳肩 不在乎 不知道 可能 大概 應該 我猜 聳肩 都好 隨便|1F937-1F3FB-200D-2640-FE0F:🤷🏻‍♀️ 1F937-1F3FC-200D-2640-FE0F:🤷🏼‍♀️ 1F937-1F3FD-200D-2640-FE0F:🤷🏽‍♀️ 1F937-1F3FE-200D-2640-FE0F:🤷🏾‍♀️ 1F937-1F3FF-200D-2640-FE0F:🤷🏿‍♀️
1|1F9D1-200D-2695-FE0F|🧑‍⚕️|health worker|醫護人員|doctor health healthcare nurse therapist worker health worker health_worker 醫護人員 治療 護士 醫生 醫護|1F9D1-1F3FB-200D-2695-FE0F:🧑🏻‍⚕️ 1F9D1-1F3FC-200D-2695-FE0F:🧑🏼‍⚕️ 1F9D1-1F3FD-200D-2695-FE0F:🧑🏽‍⚕️ 1F9D1-1F3FE-200D-2695-FE0F:🧑🏾‍⚕️ 1F9D1-1F3FF-200D-2695-FE0F:🧑🏿‍⚕️
1|1F468-200D-2695-FE0F|👨‍⚕️|man health worker|男醫生|doctor health healthcare man nurse therapist worker man health worker man_health_worker 男醫生 男 護士 醫生|1F468-1F3FB-200D-2695-FE0F:👨🏻‍⚕️ 1F468-1F3FC-200D-2695-FE0F:👨🏼‍⚕️ 1F468-1F3FD-200D-2695-FE0F:👨🏽‍⚕️ 1F468-1F3FE-200D-2695-FE0F:👨🏾‍⚕️ 1F468-1F3FF-200D-2695-FE0F:👨🏿‍⚕️
1|1F469-200D-2695-FE0F|👩‍⚕️|woman health worker|女醫生|doctor health healthcare nurse therapist woman worker woman health worker woman_health_worker 女醫生 女 治療師 護士 醫生|1F469-1F3FB-200D-2695-FE0F:👩🏻‍⚕️ 1F469-1F3FC-200D-2695-FE0F:👩🏼‍⚕️ 1F469-1F3FD-200D-2695-FE0F:👩🏽‍⚕️ 1F469-1F3FE-200D-2695-FE0F:👩🏾‍⚕️ 1F469-1F3FF-200D-2695-FE0F:👩🏿‍⚕️
1|1F9D1-200D-1F393|🧑‍🎓|student|學生|graduate student 學生 畢業|1F9D1-1F3FB-200D-1F393:🧑🏻‍🎓 1F9D1-1F3FC-200D-1F393:🧑🏼‍🎓 1F9D1-1F3FD-200D-1F393:🧑🏽‍🎓 1F9D1-1F3FE-200D-1F393:🧑🏾‍🎓 1F9D1-1F3FF-200D-1F393:🧑🏿‍🎓
1|1F468-200D-1F393|👨‍🎓|man student|男畢業生|graduate man student man student man_student 男畢業生 學生 男 畢業|1F468-1F3FB-200D-1F393:👨🏻‍🎓 1F468-1F3FC-200D-1F393:👨🏼‍🎓 1F468-1F3FD-200D-1F393:👨🏽‍🎓 1F468-1F3FE-200D-1F393:👨🏾‍🎓 1F468-1F3FF-200D-1F393:👨🏿‍🎓
1|1F469-200D-1F393|👩‍🎓|woman student|女畢業生|graduate student woman woman student woman_student 女畢業生 女 學生 畢業|1F469-1F3FB-200D-1F393:👩🏻‍🎓 1F469-1F3FC-200D-1F393:👩🏼‍🎓 1F469-1F3FD-200D-1F393:👩🏽‍🎓 1F469-1F3FE-200D-1F393:👩🏾‍🎓 1F469-1F3FF-200D-1F393:👩🏿‍🎓
1|1F9D1-200D-1F3EB|🧑‍🏫|teacher|老師|instructor lecturer professor teacher 老師 教師 教授|1F9D1-1F3FB-200D-1F3EB:🧑🏻‍🏫 1F9D1-1F3FC-200D-1F3EB:🧑🏼‍🏫 1F9D1-1F3FD-200D-1F3EB:🧑🏽‍🏫 1F9D1-1F3FE-200D-1F3EB:🧑🏾‍🏫 1F9D1-1F3FF-200D-1F3EB:🧑🏿‍🏫
1|1F468-200D-1F3EB|👨‍🏫|man teacher|男老師|instructor lecturer man professor teacher man teacher man_teacher 男老師 教授 男 老師 講師|1F468-1F3FB-200D-1F3EB:👨🏻‍🏫 1F468-1F3FC-200D-1F3EB:👨🏼‍🏫 1F468-1F3FD-200D-1F3EB:👨🏽‍🏫 1F468-1F3FE-200D-1F3EB:👨🏾‍🏫 1F468-1F3FF-200D-1F3EB:👨🏿‍🏫
1|1F469-200D-1F3EB|👩‍🏫|woman teacher|女老師|instructor lecturer professor teacher woman woman teacher woman_teacher 女老師 女 教授 老師 講師|1F469-1F3FB-200D-1F3EB:👩🏻‍🏫 1F469-1F3FC-200D-1F3EB:👩🏼‍🏫 1F469-1F3FD-200D-1F3EB:👩🏽‍🏫 1F469-1F3FE-200D-1F3EB:👩🏾‍🏫 1F469-1F3FF-200D-1F3EB:👩🏿‍🏫
1|1F9D1-200D-2696-FE0F|🧑‍⚖️|judge|法官|justice law scales judge 法官 正義|1F9D1-1F3FB-200D-2696-FE0F:🧑🏻‍⚖️ 1F9D1-1F3FC-200D-2696-FE0F:🧑🏼‍⚖️ 1F9D1-1F3FD-200D-2696-FE0F:🧑🏽‍⚖️ 1F9D1-1F3FE-200D-2696-FE0F:🧑🏾‍⚖️ 1F9D1-1F3FF-200D-2696-FE0F:🧑🏿‍⚖️
1|1F468-200D-2696-FE0F|👨‍⚖️|man judge|男法官|judge justice law man scales man judge man_judge 男法官 法官 男|1F468-1F3FB-200D-2696-FE0F:👨🏻‍⚖️ 1F468-1F3FC-200D-2696-FE0F:👨🏼‍⚖️ 1F468-1F3FD-200D-2696-FE0F:👨🏽‍⚖️ 1F468-1F3FE-200D-2696-FE0F:👨🏾‍⚖️ 1F468-1F3FF-200D-2696-FE0F:👨🏿‍⚖️
1|1F469-200D-2696-FE0F|👩‍⚖️|woman judge|女法官|judge justice law scales woman woman judge woman_judge 女法官 女 法官|1F469-1F3FB-200D-2696-FE0F:👩🏻‍⚖️ 1F469-1F3FC-200D-2696-FE0F:👩🏼‍⚖️ 1F469-1F3FD-200D-2696-FE0F:👩🏽‍⚖️ 1F469-1F3FE-200D-2696-FE0F:👩🏾‍⚖️ 1F469-1F3FF-200D-2696-FE0F:👩🏿‍⚖️
1|1F9D1-200D-1F33E|🧑‍🌾|farmer|農民|gardener rancher farmer 農民 園丁 農人 農夫|1F9D1-1F3FB-200D-1F33E:🧑🏻‍🌾 1F9D1-1F3FC-200D-1F33E:🧑🏼‍🌾 1F9D1-1F3FD-200D-1F33E:🧑🏽‍🌾 1F9D1-1F3FE-200D-1F33E:🧑🏾‍🌾 1F9D1-1F3FF-200D-1F33E:🧑🏿‍🌾
1|1F468-200D-1F33E|👨‍🌾|man farmer|農夫|farmer gardener man rancher man farmer man_farmer 農夫 園丁 男 農民|1F468-1F3FB-200D-1F33E:👨🏻‍🌾 1F468-1F3FC-200D-1F33E:👨🏼‍🌾 1F468-1F3FD-200D-1F33E:👨🏽‍🌾 1F468-1F3FE-200D-1F33E:👨🏾‍🌾 1F468-1F3FF-200D-1F33E:👨🏿‍🌾
1|1F469-200D-1F33E|👩‍🌾|woman farmer|農婦|farmer gardener rancher woman woman farmer woman_farmer 農婦 園丁 女 農民|1F469-1F3FB-200D-1F33E:👩🏻‍🌾 1F469-1F3FC-200D-1F33E:👩🏼‍🌾 1F469-1F3FD-200D-1F33E:👩🏽‍🌾 1F469-1F3FE-200D-1F33E:👩🏾‍🌾 1F469-1F3FF-200D-1F33E:👩🏿‍🌾
1|1F9D1-200D-1F373|🧑‍🍳|cook|廚師|chef cook 廚師 烹飪|1F9D1-1F3FB-200D-1F373:🧑🏻‍🍳 1F9D1-1F3FC-200D-1F373:🧑🏼‍🍳 1F9D1-1F3FD-200D-1F373:🧑🏽‍🍳 1F9D1-1F3FE-200D-1F373:🧑🏾‍🍳 1F9D1-1F3FF-200D-1F373:🧑🏿‍🍳
1|1F468-200D-1F373|👨‍🍳|man cook|男廚師|chef cook man man cook man_cook 男廚師 主廚 大廚 廚師 男|1F468-1F3FB-200D-1F373:👨🏻‍🍳 1F468-1F3FC-200D-1F373:👨🏼‍🍳 1F468-1F3FD-200D-1F373:👨🏽‍🍳 1F468-1F3FE-200D-1F373:👨🏾‍🍳 1F468-1F3FF-200D-1F373:👨🏿‍🍳
1|1F469-200D-1F373|👩‍🍳|woman cook|女廚師|chef cook woman woman cook woman_cook 女廚師 主廚 大廚 女 廚師|1F469-1F3FB-200D-1F373:👩🏻‍🍳 1F469-1F3FC-200D-1F373:👩🏼‍🍳 1F469-1F3FD-200D-1F373:👩🏽‍🍳 1F469-1F3FE-200D-1F373:👩🏾‍🍳 1F469-1F3FF-200D-1F373:👩🏿‍🍳
1|1F9D1-200D-1F527|🧑‍🔧|mechanic|技工|electrician plumber tradesperson mechanic 技工 修理 工人 師傅|1F9D1-1F3FB-200D-1F527:🧑🏻‍🔧 1F9D1-1F3FC-200D-1F527:🧑🏼‍🔧 1F9D1-1F3FD-200D-1F527:🧑🏽‍🔧 1F9D1-1F3FE-200D-1F527:🧑🏾‍🔧 1F9D1-1F3FF-200D-1F527:🧑🏿‍🔧
1|1F468-200D-1F527|👨‍🔧|man mechanic|男技工|electrician man mechanic plumber tradesperson man mechanic man_mechanic 男技工 技工 機械技師 男 電工 黑手|1F468-1F3FB-200D-1F527:👨🏻‍🔧 1F468-1F3FC-200D-1F527:👨🏼‍🔧 1F468-1F3FD-200D-1F527:👨🏽‍🔧 1F468-1F3FE-200D-1F527:👨🏾‍🔧 1F468-1F3FF-200D-1F527:👨🏿‍🔧
1|1F469-200D-1F527|👩‍🔧|woman mechanic|女技工|electrician mechanic plumber tradesperson woman woman mechanic woman_mechanic 女技工 女 技工 機械技師 水管工人 黑手|1F469-1F3FB-200D-1F527:👩🏻‍🔧 1F469-1F3FC-200D-1F527:👩🏼‍🔧 1F469-1F3FD-200D-1F527:👩🏽‍🔧 1F469-1F3FE-200D-1F527:👩🏾‍🔧 1F469-1F3FF-200D-1F527:👩🏿‍🔧
1|1F9D1-200D-1F3ED|🧑‍🏭|factory worker|工廠作業員|assembly factory industrial worker factory worker factory_worker 工廠作業員 作業員 工廠 工業 焊接|1F9D1-1F3FB-200D-1F3ED:🧑🏻‍🏭 1F9D1-1F3FC-200D-1F3ED:🧑🏼‍🏭 1F9D1-1F3FD-200D-1F3ED:🧑🏽‍🏭 1F9D1-1F3FE-200D-1F3ED:🧑🏾‍🏭 1F9D1-1F3FF-200D-1F3ED:🧑🏿‍🏭
1|1F468-200D-1F3ED|👨‍🏭|man factory worker|工廠男作業員|assembly factory industrial man worker man factory worker man_factory_worker 工廠男作業員 作業員 工廠 男 男工人|1F468-1F3FB-200D-1F3ED:👨🏻‍🏭 1F468-1F3FC-200D-1F3ED:👨🏼‍🏭 1F468-1F3FD-200D-1F3ED:👨🏽‍🏭 1F468-1F3FE-200D-1F3ED:👨🏾‍🏭 1F468-1F3FF-200D-1F3ED:👨🏿‍🏭
1|1F469-200D-1F3ED|👩‍🏭|woman factory worker|工廠女作業員|assembly factory industrial woman worker woman factory worker woman_factory_worker 工廠女作業員 作業員 女 工廠|1F469-1F3FB-200D-1F3ED:👩🏻‍🏭 1F469-1F3FC-200D-1F3ED:👩🏼‍🏭 1F469-1F3FD-200D-1F3ED:👩🏽‍🏭 1F469-1F3FE-200D-1F3ED:👩🏾‍🏭 1F469-1F3FF-200D-1F3ED:👩🏿‍🏭
1|1F9D1-200D-1F4BC|🧑‍💼|office worker|上班族|architect business manager office white-collar worker office worker office_worker 上班族 商務 白領 經理|1F9D1-1F3FB-200D-1F4BC:🧑🏻‍💼 1F9D1-1F3FC-200D-1F4BC:🧑🏼‍💼 1F9D1-1F3FD-200D-1F4BC:🧑🏽‍💼 1F9D1-1F3FE-200D-1F4BC:🧑🏾‍💼 1F9D1-1F3FF-200D-1F4BC:🧑🏿‍💼
1|1F468-200D-1F4BC|👨‍💼|man office worker|男性上班族|architect business man manager office white-collar worker man office worker man_office_worker 男性上班族 上班族 建築師 男 白領|1F468-1F3FB-200D-1F4BC:👨🏻‍💼 1F468-1F3FC-200D-1F4BC:👨🏼‍💼 1F468-1F3FD-200D-1F4BC:👨🏽‍💼 1F468-1F3FE-200D-1F4BC:👨🏾‍💼 1F468-1F3FF-200D-1F4BC:👨🏿‍💼
1|1F469-200D-1F4BC|👩‍💼|woman office worker|女性上班族|architect business manager office white-collar woman worker woman office worker woman_office_worker 女性上班族 上班族 女 白領 經理|1F469-1F3FB-200D-1F4BC:👩🏻‍💼 1F469-1F3FC-200D-1F4BC:👩🏼‍💼 1F469-1F3FD-200D-1F4BC:👩🏽‍💼 1F469-1F3FE-200D-1F4BC:👩🏾‍💼 1F469-1F3FF-200D-1F4BC:👩🏿‍💼
1|1F9D1-200D-1F52C|🧑‍🔬|scientist|科學家|biologist chemist engineer mathematician physicist scientist 科學家 化學家 工程師 物理學家 生物學家|1F9D1-1F3FB-200D-1F52C:🧑🏻‍🔬 1F9D1-1F3FC-200D-1F52C:🧑🏼‍🔬 1F9D1-1F3FD-200D-1F52C:🧑🏽‍🔬 1F9D1-1F3FE-200D-1F52C:🧑🏾‍🔬 1F9D1-1F3FF-200D-1F52C:🧑🏿‍🔬
1|1F468-200D-1F52C|👨‍🔬|man scientist|男科學家|biologist chemist engineer man mathematician physicist scientist man scientist man_scientist 男科學家 化學家 工程師 數學家 物理學家 生物學家 男 科學家|1F468-1F3FB-200D-1F52C:👨🏻‍🔬 1F468-1F3FC-200D-1F52C:👨🏼‍🔬 1F468-1F3FD-200D-1F52C:👨🏽‍🔬 1F468-1F3FE-200D-1F52C:👨🏾‍🔬 1F468-1F3FF-200D-1F52C:👨🏿‍🔬
1|1F469-200D-1F52C|👩‍🔬|woman scientist|女科學家|biologist chemist engineer mathematician physicist scientist woman woman scientist woman_scientist 女科學家 化學家 女 工程師 數學家 物理學家 科學家|1F469-1F3FB-200D-1F52C:👩🏻‍🔬 1F469-1F3FC-200D-1F52C:👩🏼‍🔬 1F469-1F3FD-200D-1F52C:👩🏽‍🔬 1F469-1F3FE-200D-1F52C:👩🏾‍🔬 1F469-1F3FF-200D-1F52C:👩🏿‍🔬
1|1F9D1-200D-1F4BB|🧑‍💻|technologist|工程師|coder computer developer inventor software technologist 工程師 發明家 程式設計師 軟體 開發人員|1F9D1-1F3FB-200D-1F4BB:🧑🏻‍💻 1F9D1-1F3FC-200D-1F4BB:🧑🏼‍💻 1F9D1-1F3FD-200D-1F4BB:🧑🏽‍💻 1F9D1-1F3FE-200D-1F4BB:🧑🏾‍💻 1F9D1-1F3FF-200D-1F4BB:🧑🏿‍💻
1|1F468-200D-1F4BB|👨‍💻|man technologist|男工程師|coder computer developer inventor man software technologist man technologist man_technologist 男工程師 工程師 男 發明家 程式工程師 軟體工程師|1F468-1F3FB-200D-1F4BB:👨🏻‍💻 1F468-1F3FC-200D-1F4BB:👨🏼‍💻 1F468-1F3FD-200D-1F4BB:👨🏽‍💻 1F468-1F3FE-200D-1F4BB:👨🏾‍💻 1F468-1F3FF-200D-1F4BB:👨🏿‍💻
1|1F469-200D-1F4BB|👩‍💻|woman technologist|女工程師|coder computer developer inventor software technologist woman woman technologist woman_technologist 女工程師 城市工程師 女 工程師 軟體工程師|1F469-1F3FB-200D-1F4BB:👩🏻‍💻 1F469-1F3FC-200D-1F4BB:👩🏼‍💻 1F469-1F3FD-200D-1F4BB:👩🏽‍💻 1F469-1F3FE-200D-1F4BB:👩🏾‍💻 1F469-1F3FF-200D-1F4BB:👩🏿‍💻
1|1F9D1-200D-1F3A4|🧑‍🎤|singer|歌手|actor entertainer rock rockstar star singer 歌手 搖滾 明星 演員 藝人|1F9D1-1F3FB-200D-1F3A4:🧑🏻‍🎤 1F9D1-1F3FC-200D-1F3A4:🧑🏼‍🎤 1F9D1-1F3FD-200D-1F3A4:🧑🏽‍🎤 1F9D1-1F3FE-200D-1F3A4:🧑🏾‍🎤 1F9D1-1F3FF-200D-1F3A4:🧑🏿‍🎤
1|1F468-200D-1F3A4|👨‍🎤|man singer|男歌手|actor entertainer man rock rockstar singer star man singer man_singer 男歌手 搖滾 明星 歌手 男|1F468-1F3FB-200D-1F3A4:👨🏻‍🎤 1F468-1F3FC-200D-1F3A4:👨🏼‍🎤 1F468-1F3FD-200D-1F3A4:👨🏽‍🎤 1F468-1F3FE-200D-1F3A4:👨🏾‍🎤 1F468-1F3FF-200D-1F3A4:👨🏿‍🎤
1|1F469-200D-1F3A4|👩‍🎤|woman singer|女歌手|actor entertainer rock rockstar singer star woman woman singer woman_singer 女歌手 女 搖滾 明星 歌手|1F469-1F3FB-200D-1F3A4:👩🏻‍🎤 1F469-1F3FC-200D-1F3A4:👩🏼‍🎤 1F469-1F3FD-200D-1F3A4:👩🏽‍🎤 1F469-1F3FE-200D-1F3A4:👩🏾‍🎤 1F469-1F3FF-200D-1F3A4:👩🏿‍🎤
1|1F9D1-200D-1F3A8|🧑‍🎨|artist|藝術家|palette artist 藝術家 調色盤|1F9D1-1F3FB-200D-1F3A8:🧑🏻‍🎨 1F9D1-1F3FC-200D-1F3A8:🧑🏼‍🎨 1F9D1-1F3FD-200D-1F3A8:🧑🏽‍🎨 1F9D1-1F3FE-200D-1F3A8:🧑🏾‍🎨 1F9D1-1F3FF-200D-1F3A8:🧑🏿‍🎨
1|1F468-200D-1F3A8|👨‍🎨|man artist|男藝術家|artist man palette man artist man_artist 男藝術家 男 藝術家 調色盤|1F468-1F3FB-200D-1F3A8:👨🏻‍🎨 1F468-1F3FC-200D-1F3A8:👨🏼‍🎨 1F468-1F3FD-200D-1F3A8:👨🏽‍🎨 1F468-1F3FE-200D-1F3A8:👨🏾‍🎨 1F468-1F3FF-200D-1F3A8:👨🏿‍🎨
"""

private const val EMOJI_ROWS_2 = """
1|1F469-200D-1F3A8|👩‍🎨|woman artist|女藝術家|artist palette woman woman artist woman_artist 女藝術家 女 藝術家 調色盤|1F469-1F3FB-200D-1F3A8:👩🏻‍🎨 1F469-1F3FC-200D-1F3A8:👩🏼‍🎨 1F469-1F3FD-200D-1F3A8:👩🏽‍🎨 1F469-1F3FE-200D-1F3A8:👩🏾‍🎨 1F469-1F3FF-200D-1F3A8:👩🏿‍🎨
1|1F9D1-200D-2708-FE0F|🧑‍✈️|pilot|機長|plane pilot 機長 飛機|1F9D1-1F3FB-200D-2708-FE0F:🧑🏻‍✈️ 1F9D1-1F3FC-200D-2708-FE0F:🧑🏼‍✈️ 1F9D1-1F3FD-200D-2708-FE0F:🧑🏽‍✈️ 1F9D1-1F3FE-200D-2708-FE0F:🧑🏾‍✈️ 1F9D1-1F3FF-200D-2708-FE0F:🧑🏿‍✈️
1|1F468-200D-2708-FE0F|👨‍✈️|man pilot|男機長|man pilot plane man pilot man_pilot 男機長 機師 機長 男 飛行員|1F468-1F3FB-200D-2708-FE0F:👨🏻‍✈️ 1F468-1F3FC-200D-2708-FE0F:👨🏼‍✈️ 1F468-1F3FD-200D-2708-FE0F:👨🏽‍✈️ 1F468-1F3FE-200D-2708-FE0F:👨🏾‍✈️ 1F468-1F3FF-200D-2708-FE0F:👨🏿‍✈️
1|1F469-200D-2708-FE0F|👩‍✈️|woman pilot|女機長|pilot plane woman woman pilot woman_pilot 女機長 女 女機師 機長 飛行員|1F469-1F3FB-200D-2708-FE0F:👩🏻‍✈️ 1F469-1F3FC-200D-2708-FE0F:👩🏼‍✈️ 1F469-1F3FD-200D-2708-FE0F:👩🏽‍✈️ 1F469-1F3FE-200D-2708-FE0F:👩🏾‍✈️ 1F469-1F3FF-200D-2708-FE0F:👩🏿‍✈️
1|1F9D1-200D-1F680|🧑‍🚀|astronaut|太空人|rocket space astronaut 太空人 火箭|1F9D1-1F3FB-200D-1F680:🧑🏻‍🚀 1F9D1-1F3FC-200D-1F680:🧑🏼‍🚀 1F9D1-1F3FD-200D-1F680:🧑🏽‍🚀 1F9D1-1F3FE-200D-1F680:🧑🏾‍🚀 1F9D1-1F3FF-200D-1F680:🧑🏿‍🚀
1|1F468-200D-1F680|👨‍🚀|man astronaut|男太空人|astronaut man rocket space man astronaut man_astronaut 男太空人 太空人 火箭人 男|1F468-1F3FB-200D-1F680:👨🏻‍🚀 1F468-1F3FC-200D-1F680:👨🏼‍🚀 1F468-1F3FD-200D-1F680:👨🏽‍🚀 1F468-1F3FE-200D-1F680:👨🏾‍🚀 1F468-1F3FF-200D-1F680:👨🏿‍🚀
1|1F469-200D-1F680|👩‍🚀|woman astronaut|女太空人|astronaut rocket space woman woman astronaut woman_astronaut 女太空人 太空人 女|1F469-1F3FB-200D-1F680:👩🏻‍🚀 1F469-1F3FC-200D-1F680:👩🏼‍🚀 1F469-1F3FD-200D-1F680:👩🏽‍🚀 1F469-1F3FE-200D-1F680:👩🏾‍🚀 1F469-1F3FF-200D-1F680:👩🏿‍🚀
1|1F9D1-200D-1F692|🧑‍🚒|firefighter|消防員|fire firetruck firefighter 消防員 消防車|1F9D1-1F3FB-200D-1F692:🧑🏻‍🚒 1F9D1-1F3FC-200D-1F692:🧑🏼‍🚒 1F9D1-1F3FD-200D-1F692:🧑🏽‍🚒 1F9D1-1F3FE-200D-1F692:🧑🏾‍🚒 1F9D1-1F3FF-200D-1F692:🧑🏿‍🚒
1|1F468-200D-1F692|👨‍🚒|man firefighter|男消防員|fire firefighter firetruck man man firefighter man_firefighter 男消防員 消防員 消防車 男|1F468-1F3FB-200D-1F692:👨🏻‍🚒 1F468-1F3FC-200D-1F692:👨🏼‍🚒 1F468-1F3FD-200D-1F692:👨🏽‍🚒 1F468-1F3FE-200D-1F692:👨🏾‍🚒 1F468-1F3FF-200D-1F692:👨🏿‍🚒
1|1F469-200D-1F692|👩‍🚒|woman firefighter|女消防員|fire firefighter firetruck woman woman firefighter woman_firefighter 女消防員 女 消防員|1F469-1F3FB-200D-1F692:👩🏻‍🚒 1F469-1F3FC-200D-1F692:👩🏼‍🚒 1F469-1F3FD-200D-1F692:👩🏽‍🚒 1F469-1F3FE-200D-1F692:👩🏾‍🚒 1F469-1F3FF-200D-1F692:👩🏿‍🚒
1|1F46E|👮|police officer|警察|apprehend arrest citation cop law officer over police pulled undercover police officer police_officer 警察 執法 巡邏 法律 罰單 臨檢 調查 警官 警方 逮捕|1F46E-1F3FB:👮🏻 1F46E-1F3FC:👮🏼 1F46E-1F3FD:👮🏽 1F46E-1F3FE:👮🏾 1F46E-1F3FF:👮🏿
1|1F46E-200D-2642-FE0F|👮‍♂️|man police officer|男警|apprehend arrest citation cop law man officer over police pulled undercover man police officer policeman 男警 男 警察|1F46E-1F3FB-200D-2642-FE0F:👮🏻‍♂️ 1F46E-1F3FC-200D-2642-FE0F:👮🏼‍♂️ 1F46E-1F3FD-200D-2642-FE0F:👮🏽‍♂️ 1F46E-1F3FE-200D-2642-FE0F:👮🏾‍♂️ 1F46E-1F3FF-200D-2642-FE0F:👮🏿‍♂️
1|1F46E-200D-2640-FE0F|👮‍♀️|woman police officer|女警|apprehend arrest citation cop law officer over police pulled undercover woman woman police officer policewoman 女警 執法 巡邏 法律 罰單 臨檢 調查 警官 警察 逮捕|1F46E-1F3FB-200D-2640-FE0F:👮🏻‍♀️ 1F46E-1F3FC-200D-2640-FE0F:👮🏼‍♀️ 1F46E-1F3FD-200D-2640-FE0F:👮🏽‍♀️ 1F46E-1F3FE-200D-2640-FE0F:👮🏾‍♀️ 1F46E-1F3FF-200D-2640-FE0F:👮🏿‍♀️
1|1F575|🕵️|detective|偵探|sleuth spy detective 偵探 間諜|1F575-1F3FB:🕵🏻 1F575-1F3FC:🕵🏼 1F575-1F3FD:🕵🏽 1F575-1F3FE:🕵🏾 1F575-1F3FF:🕵🏿
1|1F575-FE0F-200D-2642-FE0F|🕵️‍♂️|man detective|男偵探|detective man sleuth spy man detective male_detective 男偵探 偵探 男|1F575-1F3FB-200D-2642-FE0F:🕵🏻‍♂️ 1F575-1F3FC-200D-2642-FE0F:🕵🏼‍♂️ 1F575-1F3FD-200D-2642-FE0F:🕵🏽‍♂️ 1F575-1F3FE-200D-2642-FE0F:🕵🏾‍♂️ 1F575-1F3FF-200D-2642-FE0F:🕵🏿‍♂️
1|1F575-FE0F-200D-2640-FE0F|🕵️‍♀️|woman detective|女偵探|detective sleuth spy woman woman detective female_detective 女偵探 偵探 女|1F575-1F3FB-200D-2640-FE0F:🕵🏻‍♀️ 1F575-1F3FC-200D-2640-FE0F:🕵🏼‍♀️ 1F575-1F3FD-200D-2640-FE0F:🕵🏽‍♀️ 1F575-1F3FE-200D-2640-FE0F:🕵🏾‍♀️ 1F575-1F3FF-200D-2640-FE0F:🕵🏿‍♀️
1|1F482|💂|guard|衛兵|buckingham helmet london palace guard 衛兵 憲兵 白金漢宮|1F482-1F3FB:💂🏻 1F482-1F3FC:💂🏼 1F482-1F3FD:💂🏽 1F482-1F3FE:💂🏾 1F482-1F3FF:💂🏿
1|1F482-200D-2642-FE0F|💂‍♂️|man guard|男衛兵|buckingham guard helmet london man palace man guard guardsman 男衛兵 憲兵 男 衛兵|1F482-1F3FB-200D-2642-FE0F:💂🏻‍♂️ 1F482-1F3FC-200D-2642-FE0F:💂🏼‍♂️ 1F482-1F3FD-200D-2642-FE0F:💂🏽‍♂️ 1F482-1F3FE-200D-2642-FE0F:💂🏾‍♂️ 1F482-1F3FF-200D-2642-FE0F:💂🏿‍♂️
1|1F482-200D-2640-FE0F|💂‍♀️|woman guard|女衛兵|buckingham guard helmet london palace woman woman guard guardswoman 女衛兵 女 憲兵 白金漢宮 衛兵|1F482-1F3FB-200D-2640-FE0F:💂🏻‍♀️ 1F482-1F3FC-200D-2640-FE0F:💂🏼‍♀️ 1F482-1F3FD-200D-2640-FE0F:💂🏽‍♀️ 1F482-1F3FE-200D-2640-FE0F:💂🏾‍♀️ 1F482-1F3FF-200D-2640-FE0F:💂🏿‍♀️
1|1F977|🥷|ninja|忍者|assassin fight fighter hidden person secret skills sly soldier stealth war ninja 忍者 刺客 士兵 戰爭 打鬥 技巧 武者 隱身|1F977-1F3FB:🥷🏻 1F977-1F3FC:🥷🏼 1F977-1F3FD:🥷🏽 1F977-1F3FE:🥷🏾 1F977-1F3FF:🥷🏿
1|1F477|👷|construction worker|建築工人|build construction fix hardhat hat man person rebuild remodel repair work worker construction worker construction_worker 建築工人 安全帽 工地 男人 維修 頭盔|1F477-1F3FB:👷🏻 1F477-1F3FC:👷🏼 1F477-1F3FD:👷🏽 1F477-1F3FE:👷🏾 1F477-1F3FF:👷🏿
1|1F477-200D-2642-FE0F|👷‍♂️|man construction worker|男建築工人|build construction fix hardhat hat man rebuild remodel repair work worker man construction worker construction_worker_man 男建築工人 建築工人 男|1F477-1F3FB-200D-2642-FE0F:👷🏻‍♂️ 1F477-1F3FC-200D-2642-FE0F:👷🏼‍♂️ 1F477-1F3FD-200D-2642-FE0F:👷🏽‍♂️ 1F477-1F3FE-200D-2642-FE0F:👷🏾‍♂️ 1F477-1F3FF-200D-2642-FE0F:👷🏿‍♂️
1|1F477-200D-2640-FE0F|👷‍♀️|woman construction worker|女建築工人|build construction fix hardhat hat man rebuild remodel repair woman work worker woman construction worker construction_worker_woman 女建築工人 女 建築工人|1F477-1F3FB-200D-2640-FE0F:👷🏻‍♀️ 1F477-1F3FC-200D-2640-FE0F:👷🏼‍♀️ 1F477-1F3FD-200D-2640-FE0F:👷🏽‍♀️ 1F477-1F3FE-200D-2640-FE0F:👷🏾‍♀️ 1F477-1F3FF-200D-2640-FE0F:👷🏿‍♀️
1|1FAC5|🫅|person with crown|戴皇冠的人|crown monarch noble person regal royal royalty person with crown person_with_crown 戴皇冠的人 君主 國王 王冠 王室 皇后 皇室 皇家 貴族|1FAC5-1F3FB:🫅🏻 1FAC5-1F3FC:🫅🏼 1FAC5-1F3FD:🫅🏽 1FAC5-1F3FE:🫅🏾 1FAC5-1F3FF:🫅🏿
1|1F934|🤴|prince|王子|crown fairy fairytale fantasy king royal royalty tale prince 王子 王 皇室 皇家|1F934-1F3FB:🤴🏻 1F934-1F3FC:🤴🏼 1F934-1F3FD:🤴🏽 1F934-1F3FE:🤴🏾 1F934-1F3FF:🤴🏿
1|1F478|👸|princess|公主|crown fairy fairytale fantasy queen royal royalty tale princess 公主 后冠 皇冠 皇后 童話|1F478-1F3FB:👸🏻 1F478-1F3FC:👸🏼 1F478-1F3FD:👸🏽 1F478-1F3FE:👸🏾 1F478-1F3FF:👸🏿
1|1F473|👳|person wearing turban|戴頭巾的人|person turban wearing person wearing turban person_with_turban 戴頭巾的人 人物 纏頭巾|1F473-1F3FB:👳🏻 1F473-1F3FC:👳🏼 1F473-1F3FD:👳🏽 1F473-1F3FE:👳🏾 1F473-1F3FF:👳🏿
1|1F473-200D-2642-FE0F|👳‍♂️|man wearing turban|纏頭男人|man turban wearing man wearing turban man_with_turban 纏頭男人 戴頭巾 男|1F473-1F3FB-200D-2642-FE0F:👳🏻‍♂️ 1F473-1F3FC-200D-2642-FE0F:👳🏼‍♂️ 1F473-1F3FD-200D-2642-FE0F:👳🏽‍♂️ 1F473-1F3FE-200D-2642-FE0F:👳🏾‍♂️ 1F473-1F3FF-200D-2642-FE0F:👳🏿‍♂️
1|1F473-200D-2640-FE0F|👳‍♀️|woman wearing turban|纏頭女人|turban wearing woman woman wearing turban woman_with_turban 纏頭女人 女 戴頭巾 纏頭巾|1F473-1F3FB-200D-2640-FE0F:👳🏻‍♀️ 1F473-1F3FC-200D-2640-FE0F:👳🏼‍♀️ 1F473-1F3FD-200D-2640-FE0F:👳🏽‍♀️ 1F473-1F3FE-200D-2640-FE0F:👳🏾‍♀️ 1F473-1F3FF-200D-2640-FE0F:👳🏿‍♀️
1|1F472|👲|person with skullcap|戴瓜皮帽的人|cap chinese gua guapi hat mao person pi skullcap person with skullcap man_with_gua_pi_mao 戴瓜皮帽的人 員外 師爺 瓜皮帽|1F472-1F3FB:👲🏻 1F472-1F3FC:👲🏼 1F472-1F3FD:👲🏽 1F472-1F3FE:👲🏾 1F472-1F3FF:👲🏿
1|1F9D5|🧕|woman with headscarf|包頭巾的女子|bandana head headscarf hijab kerchief mantilla tichel woman woman with headscarf woman_with_headscarf 包頭巾的女子 披肩頭紗 面紗 頭巾 頭巾女|1F9D5-1F3FB:🧕🏻 1F9D5-1F3FC:🧕🏼 1F9D5-1F3FD:🧕🏽 1F9D5-1F3FE:🧕🏾 1F9D5-1F3FF:🧕🏿
1|1F935|🤵|person in tuxedo|穿燕尾服的人|formal person tuxedo wedding person in tuxedo person_in_tuxedo 穿燕尾服的人 新郎 正式 燕尾服|1F935-1F3FB:🤵🏻 1F935-1F3FC:🤵🏼 1F935-1F3FD:🤵🏽 1F935-1F3FE:🤵🏾 1F935-1F3FF:🤵🏿
1|1F935-200D-2642-FE0F|🤵‍♂️|man in tuxedo|穿燕尾服的男人|formal groom man tuxedo wedding man in tuxedo man_in_tuxedo 穿燕尾服的男人 燕尾服 男人|1F935-1F3FB-200D-2642-FE0F:🤵🏻‍♂️ 1F935-1F3FC-200D-2642-FE0F:🤵🏼‍♂️ 1F935-1F3FD-200D-2642-FE0F:🤵🏽‍♂️ 1F935-1F3FE-200D-2642-FE0F:🤵🏾‍♂️ 1F935-1F3FF-200D-2642-FE0F:🤵🏿‍♂️
1|1F935-200D-2640-FE0F|🤵‍♀️|woman in tuxedo|穿燕尾服的女子|formal tuxedo wedding woman woman in tuxedo woman_in_tuxedo 穿燕尾服的女子 女子 燕尾服|1F935-1F3FB-200D-2640-FE0F:🤵🏻‍♀️ 1F935-1F3FC-200D-2640-FE0F:🤵🏼‍♀️ 1F935-1F3FD-200D-2640-FE0F:🤵🏽‍♀️ 1F935-1F3FE-200D-2640-FE0F:🤵🏾‍♀️ 1F935-1F3FF-200D-2640-FE0F:🤵🏿‍♀️
1|1F470|👰|person with veil|披著頭紗的人|person veil wedding person with veil person_with_veil 披著頭紗的人 新娘 結婚 頭紗|1F470-1F3FB:👰🏻 1F470-1F3FC:👰🏼 1F470-1F3FD:👰🏽 1F470-1F3FE:👰🏾 1F470-1F3FF:👰🏿
1|1F470-200D-2642-FE0F|👰‍♂️|man with veil|披著頭紗的男人|man veil wedding man with veil man_with_veil 披著頭紗的男人 男人 頭紗|1F470-1F3FB-200D-2642-FE0F:👰🏻‍♂️ 1F470-1F3FC-200D-2642-FE0F:👰🏼‍♂️ 1F470-1F3FD-200D-2642-FE0F:👰🏽‍♂️ 1F470-1F3FE-200D-2642-FE0F:👰🏾‍♂️ 1F470-1F3FF-200D-2642-FE0F:👰🏿‍♂️
1|1F470-200D-2640-FE0F|👰‍♀️|woman with veil|披著頭紗的女子|bride veil wedding woman woman with veil bride_with_veil woman_with_veil 披著頭紗的女子 女子 頭紗|1F470-1F3FB-200D-2640-FE0F:👰🏻‍♀️ 1F470-1F3FC-200D-2640-FE0F:👰🏼‍♀️ 1F470-1F3FD-200D-2640-FE0F:👰🏽‍♀️ 1F470-1F3FE-200D-2640-FE0F:👰🏾‍♀️ 1F470-1F3FF-200D-2640-FE0F:👰🏿‍♀️
1|1F930|🤰|pregnant woman|孕婦|pregnant woman pregnant woman pregnant_woman 孕婦 懷孕|1F930-1F3FB:🤰🏻 1F930-1F3FC:🤰🏼 1F930-1F3FD:🤰🏽 1F930-1F3FE:🤰🏾 1F930-1F3FF:🤰🏿
1|1FAC3|🫃|pregnant man|懷孕的男人|belly bloated full man overeat pregnant pregnant man pregnant_man 懷孕的男人 吃太飽的男人 懷孕 肚子 膨脹 臃腫|1FAC3-1F3FB:🫃🏻 1FAC3-1F3FC:🫃🏼 1FAC3-1F3FD:🫃🏽 1FAC3-1F3FE:🫃🏾 1FAC3-1F3FF:🫃🏿
1|1FAC4|🫄|pregnant person|懷孕的人|belly bloated full overeat person pregnant stuffed pregnant person pregnant_person 懷孕的人 吃太飽 懷孕 肚子 膨脹 臃腫|1FAC4-1F3FB:🫄🏻 1FAC4-1F3FC:🫄🏼 1FAC4-1F3FD:🫄🏽 1FAC4-1F3FE:🫄🏾 1FAC4-1F3FF:🫄🏿
1|1F931|🤱|breast-feeding|哺乳|baby breast feeding mom mother nursing woman breast-feeding breast_feeding 哺乳 乳房 嬰兒 育嬰 餵母奶|1F931-1F3FB:🤱🏻 1F931-1F3FC:🤱🏼 1F931-1F3FD:🤱🏽 1F931-1F3FE:🤱🏾 1F931-1F3FF:🤱🏿
1|1F469-200D-1F37C|👩‍🍼|woman feeding baby|正在哺乳的媽媽|baby feed feeding mom mother nanny newborn nursing woman woman feeding baby woman_feeding_baby 正在哺乳的媽媽 保姆 哺乳 媽媽 嬰兒 小北鼻 新生兒 母親 餵奶 馬麻|1F469-1F3FB-200D-1F37C:👩🏻‍🍼 1F469-1F3FC-200D-1F37C:👩🏼‍🍼 1F469-1F3FD-200D-1F37C:👩🏽‍🍼 1F469-1F3FE-200D-1F37C:👩🏾‍🍼 1F469-1F3FF-200D-1F37C:👩🏿‍🍼
1|1F468-200D-1F37C|👨‍🍼|man feeding baby|正在餵奶的爸爸|baby dad father feed feeding man nanny newborn nursing man feeding baby man_feeding_baby 正在餵奶的爸爸 保姆 嬰兒 小北鼻 新生兒 父親 爸爸 男人 男性 餵奶|1F468-1F3FB-200D-1F37C:👨🏻‍🍼 1F468-1F3FC-200D-1F37C:👨🏼‍🍼 1F468-1F3FD-200D-1F37C:👨🏽‍🍼 1F468-1F3FE-200D-1F37C:👨🏾‍🍼 1F468-1F3FF-200D-1F37C:👨🏿‍🍼
1|1F9D1-200D-1F37C|🧑‍🍼|person feeding baby|正在哺乳的人|baby feed feeding nanny newborn nursing parent person feeding baby person_feeding_baby 正在哺乳的人 保姆 哺乳 媽媽 嬰兒 小北鼻 小寶貝 新生兒 爸爸 餵奶|1F9D1-1F3FB-200D-1F37C:🧑🏻‍🍼 1F9D1-1F3FC-200D-1F37C:🧑🏼‍🍼 1F9D1-1F3FD-200D-1F37C:🧑🏽‍🍼 1F9D1-1F3FE-200D-1F37C:🧑🏾‍🍼 1F9D1-1F3FF-200D-1F37C:🧑🏿‍🍼
1|1F47C|👼|baby angel|天使|angel baby church face fairy fairytale fantasy tale baby angel 天使 兒童 孩子 小天使|1F47C-1F3FB:👼🏻 1F47C-1F3FC:👼🏼 1F47C-1F3FD:👼🏽 1F47C-1F3FE:👼🏾 1F47C-1F3FF:👼🏿
1|1F385|🎅|Santa Claus|聖誕老人|celebration christmas claus fairy fantasy father holiday merry santa tale xmas santa claus 聖誕老人 爸爸 聖誕 聖誕老公公|1F385-1F3FB:🎅🏻 1F385-1F3FC:🎅🏼 1F385-1F3FD:🎅🏽 1F385-1F3FE:🎅🏾 1F385-1F3FF:🎅🏿
1|1F936|🤶|Mrs. Claus|聖誕老奶奶|celebration christmas claus fairy fantasy holiday merry mother mrs santa tale xmas mrs. claus mrs_claus 聖誕老奶奶 媽媽 童話 聖誕節|1F936-1F3FB:🤶🏻 1F936-1F3FC:🤶🏼 1F936-1F3FD:🤶🏽 1F936-1F3FE:🤶🏾 1F936-1F3FF:🤶🏿
1|1F9D1-200D-1F384|🧑‍🎄|Mx Claus|跨性別聖誕老人|celebration christmas claus fairy fantasy holiday merry mx santa tale xmas mx claus mx_claus 跨性別聖誕老人 節日 耶誕快樂 耶誕節 耶誕老人 聖誕 聖誕快樂 聖誕老人|1F9D1-1F3FB-200D-1F384:🧑🏻‍🎄 1F9D1-1F3FC-200D-1F384:🧑🏼‍🎄 1F9D1-1F3FD-200D-1F384:🧑🏽‍🎄 1F9D1-1F3FE-200D-1F384:🧑🏾‍🎄 1F9D1-1F3FF-200D-1F384:🧑🏿‍🎄
1|1F9B8|🦸|superhero|超級英雄|good hero superpower superhero 超級英雄 正派 英雄 超人 超能力|1F9B8-1F3FB:🦸🏻 1F9B8-1F3FC:🦸🏼 1F9B8-1F3FD:🦸🏽 1F9B8-1F3FE:🦸🏾 1F9B8-1F3FF:🦸🏿
1|1F9B8-200D-2642-FE0F|🦸‍♂️|man superhero|超人|good hero man superhero superpower man superhero superhero_man 超人 天賦 男人 英雄 超能力|1F9B8-1F3FB-200D-2642-FE0F:🦸🏻‍♂️ 1F9B8-1F3FC-200D-2642-FE0F:🦸🏼‍♂️ 1F9B8-1F3FD-200D-2642-FE0F:🦸🏽‍♂️ 1F9B8-1F3FE-200D-2642-FE0F:🦸🏾‍♂️ 1F9B8-1F3FF-200D-2642-FE0F:🦸🏿‍♂️
1|1F9B8-200D-2640-FE0F|🦸‍♀️|woman superhero|女超人|good hero heroine superhero superpower woman woman superhero superhero_woman 女超人 天賦 女英雄 英雄 超能力|1F9B8-1F3FB-200D-2640-FE0F:🦸🏻‍♀️ 1F9B8-1F3FC-200D-2640-FE0F:🦸🏼‍♀️ 1F9B8-1F3FD-200D-2640-FE0F:🦸🏽‍♀️ 1F9B8-1F3FE-200D-2640-FE0F:🦸🏾‍♀️ 1F9B8-1F3FF-200D-2640-FE0F:🦸🏿‍♀️
1|1F9B9|🦹|supervillain|超級反派|bad criminal evil superpower villain supervillain 超級反派 壞 惡棍 犯罪 罪犯 超級惡棍 超能力 邪惡|1F9B9-1F3FB:🦹🏻 1F9B9-1F3FC:🦹🏼 1F9B9-1F3FD:🦹🏽 1F9B9-1F3FE:🦹🏾 1F9B9-1F3FF:🦹🏿
1|1F9B9-200D-2642-FE0F|🦹‍♂️|man supervillain|男超級反派|bad criminal evil man superpower supervillain villain man supervillain supervillain_man 男超級反派 反派 犯罪 男人 超能力 邪惡|1F9B9-1F3FB-200D-2642-FE0F:🦹🏻‍♂️ 1F9B9-1F3FC-200D-2642-FE0F:🦹🏼‍♂️ 1F9B9-1F3FD-200D-2642-FE0F:🦹🏽‍♂️ 1F9B9-1F3FE-200D-2642-FE0F:🦹🏾‍♂️ 1F9B9-1F3FF-200D-2642-FE0F:🦹🏿‍♂️
1|1F9B9-200D-2640-FE0F|🦹‍♀️|woman supervillain|女超級反派|bad criminal evil superpower supervillain villain woman woman supervillain supervillain_woman 女超級反派 反派 女人 犯罪 超能力 邪惡|1F9B9-1F3FB-200D-2640-FE0F:🦹🏻‍♀️ 1F9B9-1F3FC-200D-2640-FE0F:🦹🏼‍♀️ 1F9B9-1F3FD-200D-2640-FE0F:🦹🏽‍♀️ 1F9B9-1F3FE-200D-2640-FE0F:🦹🏾‍♀️ 1F9B9-1F3FF-200D-2640-FE0F:🦹🏿‍♀️
1|1F9D9|🧙|mage|魔術師|fantasy magic play sorcerer sorceress sorcery spell summon witch wizard mage 魔術師 巫師 男巫 著魔 魔咒|1F9D9-1F3FB:🧙🏻 1F9D9-1F3FC:🧙🏼 1F9D9-1F3FD:🧙🏽 1F9D9-1F3FE:🧙🏾 1F9D9-1F3FF:🧙🏿
1|1F9D9-200D-2642-FE0F|🧙‍♂️|man mage|男巫|fantasy mage magic man play sorcerer sorceress sorcery spell summon witch wizard man mage mage_man 男巫 男巫|1F9D9-1F3FB-200D-2642-FE0F:🧙🏻‍♂️ 1F9D9-1F3FC-200D-2642-FE0F:🧙🏼‍♂️ 1F9D9-1F3FD-200D-2642-FE0F:🧙🏽‍♂️ 1F9D9-1F3FE-200D-2642-FE0F:🧙🏾‍♂️ 1F9D9-1F3FF-200D-2642-FE0F:🧙🏿‍♂️
1|1F9D9-200D-2640-FE0F|🧙‍♀️|woman mage|女巫師|fantasy mage magic play sorcerer sorceress sorcery spell summon witch wizard woman woman mage mage_woman 女巫師 女巫師|1F9D9-1F3FB-200D-2640-FE0F:🧙🏻‍♀️ 1F9D9-1F3FC-200D-2640-FE0F:🧙🏼‍♀️ 1F9D9-1F3FD-200D-2640-FE0F:🧙🏽‍♀️ 1F9D9-1F3FE-200D-2640-FE0F:🧙🏾‍♀️ 1F9D9-1F3FF-200D-2640-FE0F:🧙🏿‍♀️
1|1F9DA|🧚|fairy|仙女|fairytale fantasy myth person pixie tale wings fairy 仙女 仙子 翅膀|1F9DA-1F3FB:🧚🏻 1F9DA-1F3FC:🧚🏼 1F9DA-1F3FD:🧚🏽 1F9DA-1F3FE:🧚🏾 1F9DA-1F3FF:🧚🏿
1|1F9DA-200D-2642-FE0F|🧚‍♂️|man fairy|男妖精|fairy fairytale fantasy man myth oberon person pixie puck tale wings man fairy fairy_man 男妖精 男妖精|1F9DA-1F3FB-200D-2642-FE0F:🧚🏻‍♂️ 1F9DA-1F3FC-200D-2642-FE0F:🧚🏼‍♂️ 1F9DA-1F3FD-200D-2642-FE0F:🧚🏽‍♂️ 1F9DA-1F3FE-200D-2642-FE0F:🧚🏾‍♂️ 1F9DA-1F3FF-200D-2642-FE0F:🧚🏿‍♂️
1|1F9DA-200D-2640-FE0F|🧚‍♀️|woman fairy|女妖精|fairy fairytale fantasy myth person pixie tale titania wings woman woman fairy fairy_woman 女妖精 女妖精|1F9DA-1F3FB-200D-2640-FE0F:🧚🏻‍♀️ 1F9DA-1F3FC-200D-2640-FE0F:🧚🏼‍♀️ 1F9DA-1F3FD-200D-2640-FE0F:🧚🏽‍♀️ 1F9DA-1F3FE-200D-2640-FE0F:🧚🏾‍♀️ 1F9DA-1F3FF-200D-2640-FE0F:🧚🏿‍♀️
1|1F9DB|🧛|vampire|吸血鬼|blood dracula fangs halloween scary supernatural teeth undead vampire 吸血鬼 尖牙 毒牙 牙齒|1F9DB-1F3FB:🧛🏻 1F9DB-1F3FC:🧛🏼 1F9DB-1F3FD:🧛🏽 1F9DB-1F3FE:🧛🏾 1F9DB-1F3FF:🧛🏿
1|1F9DB-200D-2642-FE0F|🧛‍♂️|man vampire|男吸血鬼|blood fangs halloween man scary supernatural teeth undead vampire man vampire vampire_man 男吸血鬼 男吸血鬼|1F9DB-1F3FB-200D-2642-FE0F:🧛🏻‍♂️ 1F9DB-1F3FC-200D-2642-FE0F:🧛🏼‍♂️ 1F9DB-1F3FD-200D-2642-FE0F:🧛🏽‍♂️ 1F9DB-1F3FE-200D-2642-FE0F:🧛🏾‍♂️ 1F9DB-1F3FF-200D-2642-FE0F:🧛🏿‍♂️
1|1F9DB-200D-2640-FE0F|🧛‍♀️|woman vampire|女吸血鬼|blood fangs halloween scary supernatural teeth undead vampire woman woman vampire vampire_woman 女吸血鬼 女吸血鬼|1F9DB-1F3FB-200D-2640-FE0F:🧛🏻‍♀️ 1F9DB-1F3FC-200D-2640-FE0F:🧛🏼‍♀️ 1F9DB-1F3FD-200D-2640-FE0F:🧛🏽‍♀️ 1F9DB-1F3FE-200D-2640-FE0F:🧛🏾‍♀️ 1F9DB-1F3FF-200D-2640-FE0F:🧛🏿‍♀️
1|1F9DC|🧜|merperson|人魚|creature fairytale folklore ocean sea siren trident merperson 人魚 三叉戟 傳說 塞壬 海中 海底 童話 美人魚 龍宮|1F9DC-1F3FB:🧜🏻 1F9DC-1F3FC:🧜🏼 1F9DC-1F3FD:🧜🏽 1F9DC-1F3FE:🧜🏾 1F9DC-1F3FF:🧜🏿
1|1F9DC-200D-2642-FE0F|🧜‍♂️|merman|男人魚|creature fairytale folklore neptune ocean poseidon sea siren trident triton merman 男人魚 男人魚|1F9DC-1F3FB-200D-2642-FE0F:🧜🏻‍♂️ 1F9DC-1F3FC-200D-2642-FE0F:🧜🏼‍♂️ 1F9DC-1F3FD-200D-2642-FE0F:🧜🏽‍♂️ 1F9DC-1F3FE-200D-2642-FE0F:🧜🏾‍♂️ 1F9DC-1F3FF-200D-2642-FE0F:🧜🏿‍♂️
1|1F9DC-200D-2640-FE0F|🧜‍♀️|mermaid|美人魚|creature fairytale folklore merwoman ocean sea siren trident mermaid 美人魚 美人魚|1F9DC-1F3FB-200D-2640-FE0F:🧜🏻‍♀️ 1F9DC-1F3FC-200D-2640-FE0F:🧜🏼‍♀️ 1F9DC-1F3FD-200D-2640-FE0F:🧜🏽‍♀️ 1F9DC-1F3FE-200D-2640-FE0F:🧜🏾‍♀️ 1F9DC-1F3FF-200D-2640-FE0F:🧜🏿‍♀️
1|1F9DD|🧝|elf|小精靈|elves enchantment fantasy folklore magic magical myth elf 小精靈 勒苟拉斯 魔戒風|1F9DD-1F3FB:🧝🏻 1F9DD-1F3FC:🧝🏼 1F9DD-1F3FD:🧝🏽 1F9DD-1F3FE:🧝🏾 1F9DD-1F3FF:🧝🏿
1|1F9DD-200D-2642-FE0F|🧝‍♂️|man elf|男精靈|elf elves enchantment fantasy folklore magic magical man myth man elf elf_man 男精靈 男精靈|1F9DD-1F3FB-200D-2642-FE0F:🧝🏻‍♂️ 1F9DD-1F3FC-200D-2642-FE0F:🧝🏼‍♂️ 1F9DD-1F3FD-200D-2642-FE0F:🧝🏽‍♂️ 1F9DD-1F3FE-200D-2642-FE0F:🧝🏾‍♂️ 1F9DD-1F3FF-200D-2642-FE0F:🧝🏿‍♂️
1|1F9DD-200D-2640-FE0F|🧝‍♀️|woman elf|女精靈|elf elves enchantment fantasy folklore magic magical myth woman woman elf elf_woman 女精靈 女精靈|1F9DD-1F3FB-200D-2640-FE0F:🧝🏻‍♀️ 1F9DD-1F3FC-200D-2640-FE0F:🧝🏼‍♀️ 1F9DD-1F3FD-200D-2640-FE0F:🧝🏽‍♀️ 1F9DD-1F3FE-200D-2640-FE0F:🧝🏾‍♀️ 1F9DD-1F3FF-200D-2640-FE0F:🧝🏿‍♀️
1|1F9DE|🧞|genie|精靈|djinn fantasy jinn lamp myth rub wishes genie 精靈 傑尼|
1|1F9DE-200D-2642-FE0F|🧞‍♂️|man genie|藍精靈|djinn fantasy genie jinn lamp man myth rub wishes man genie genie_man 藍精靈 藍精靈|
1|1F9DE-200D-2640-FE0F|🧞‍♀️|woman genie|女藍精靈|djinn fantasy genie jinn lamp myth rub wishes woman woman genie genie_woman 女藍精靈 女藍精靈|
1|1F9DF|🧟|zombie|殭屍|apocalypse dead halloween horror scary undead walking zombie 殭屍 嚇人 萬聖節 行屍走肉|
1|1F9DF-200D-2642-FE0F|🧟‍♂️|man zombie|男殭屍|apocalypse dead halloween horror man scary undead walking zombie man zombie zombie_man 男殭屍 男殭屍|
1|1F9DF-200D-2640-FE0F|🧟‍♀️|woman zombie|女殭屍|apocalypse dead halloween horror scary undead walking woman zombie woman zombie zombie_woman 女殭屍 女殭屍|
1|1F9CC|🧌|troll|巨人|fairy fantasy monster tale trolling troll 巨人 山怪 幻想 怪物 怪獸 戳 童話 網路白目|
1|1FAC8|🫈|hairy creature|毛怪|bigfoot cryptid forest giant hairy sasquatch woodwose yeti hairy creature 毛怪 大腳怪 大腳野人 巨大 林中野人 森林 毛茸茸 神秘 雪怪|
1|1F486|💆|person getting massage|按摩|face getting headache massage person relax relaxing salon soothe spa tension therapy treatment person getting massage 按摩 人物 做臉 放鬆 沙龍 療程 舒爽 護膚 頭痛 馬殺雞|1F486-1F3FB:💆🏻 1F486-1F3FC:💆🏼 1F486-1F3FD:💆🏽 1F486-1F3FE:💆🏾 1F486-1F3FF:💆🏿
1|1F486-200D-2642-FE0F|💆‍♂️|man getting massage|男生按摩|face getting headache man massage relax relaxing salon soothe spa tension therapy treatment man getting massage massage_man 男生按摩 按摩 放鬆 沙龍 男 頭痛 馬殺雞|1F486-1F3FB-200D-2642-FE0F:💆🏻‍♂️ 1F486-1F3FC-200D-2642-FE0F:💆🏼‍♂️ 1F486-1F3FD-200D-2642-FE0F:💆🏽‍♂️ 1F486-1F3FE-200D-2642-FE0F:💆🏾‍♂️ 1F486-1F3FF-200D-2642-FE0F:💆🏿‍♂️
1|1F486-200D-2640-FE0F|💆‍♀️|woman getting massage|女生按摩|face getting headache massage relax relaxing salon soothe spa tension therapy treatment woman woman getting massage massage_woman 女生按摩 女 按摩 馬殺雞|1F486-1F3FB-200D-2640-FE0F:💆🏻‍♀️ 1F486-1F3FC-200D-2640-FE0F:💆🏼‍♀️ 1F486-1F3FD-200D-2640-FE0F:💆🏽‍♀️ 1F486-1F3FE-200D-2640-FE0F:💆🏾‍♀️ 1F486-1F3FF-200D-2640-FE0F:💆🏿‍♀️
1|1F487|💇|person getting haircut|理髮|barber beauty chop cosmetology cut groom hair haircut parlor person shears style person getting haircut 理髮 人物 剪頭髮 沙龍 美容 美髮師 設計師 造型 髮型 髮廊|1F487-1F3FB:💇🏻 1F487-1F3FC:💇🏼 1F487-1F3FD:💇🏽 1F487-1F3FE:💇🏾 1F487-1F3FF:💇🏿
1|1F487-200D-2642-FE0F|💇‍♂️|man getting haircut|男生理髮|barber beauty chop cosmetology cut groom hair haircut man parlor person shears style man getting haircut haircut_man 男生理髮 剪頭髮 理髮 男|1F487-1F3FB-200D-2642-FE0F:💇🏻‍♂️ 1F487-1F3FC-200D-2642-FE0F:💇🏼‍♂️ 1F487-1F3FD-200D-2642-FE0F:💇🏽‍♂️ 1F487-1F3FE-200D-2642-FE0F:💇🏾‍♂️ 1F487-1F3FF-200D-2642-FE0F:💇🏿‍♂️
1|1F487-200D-2640-FE0F|💇‍♀️|woman getting haircut|女生理髮|barber beauty chop cosmetology cut groom hair haircut parlor person shears style woman woman getting haircut haircut_woman 女生理髮 剪頭髮 女 理髮|1F487-1F3FB-200D-2640-FE0F:💇🏻‍♀️ 1F487-1F3FC-200D-2640-FE0F:💇🏼‍♀️ 1F487-1F3FD-200D-2640-FE0F:💇🏽‍♀️ 1F487-1F3FE-200D-2640-FE0F:💇🏾‍♀️ 1F487-1F3FF-200D-2640-FE0F:💇🏿‍♀️
1|1F6B6|🚶|person walking|行人|amble gait hike man pace pedestrian person stride stroll walk walking person walking 行人 人物 大搖大擺 步伐 男子走路 走路 路人 踏步|1F6B6-1F3FB:🚶🏻 1F6B6-1F3FC:🚶🏼 1F6B6-1F3FD:🚶🏽 1F6B6-1F3FE:🚶🏾 1F6B6-1F3FF:🚶🏿
1|1F6B6-200D-2642-FE0F|🚶‍♂️|man walking|男行人|amble gait hike man pace pedestrian stride stroll walk walking man walking walking_man 男行人 走路 路人|1F6B6-1F3FB-200D-2642-FE0F:🚶🏻‍♂️ 1F6B6-1F3FC-200D-2642-FE0F:🚶🏼‍♂️ 1F6B6-1F3FD-200D-2642-FE0F:🚶🏽‍♂️ 1F6B6-1F3FE-200D-2642-FE0F:🚶🏾‍♂️ 1F6B6-1F3FF-200D-2642-FE0F:🚶🏿‍♂️
1|1F6B6-200D-2640-FE0F|🚶‍♀️|woman walking|女行人|amble gait hike man pace pedestrian stride stroll walk walking woman woman walking walking_woman 女行人 漫步 走路 路人 閒逛|1F6B6-1F3FB-200D-2640-FE0F:🚶🏻‍♀️ 1F6B6-1F3FC-200D-2640-FE0F:🚶🏼‍♀️ 1F6B6-1F3FD-200D-2640-FE0F:🚶🏽‍♀️ 1F6B6-1F3FE-200D-2640-FE0F:🚶🏾‍♀️ 1F6B6-1F3FF-200D-2640-FE0F:🚶🏿‍♀️
1|1F6B6-200D-27A1-FE0F|🚶‍➡️|person walking: facing right|行人：面右|amble facing gait hike man pace pedestrian person right stride stroll walk walking person walking: facing right 行人：面右 人物 大搖大擺 步伐 男子走路 行人 走路 路人 踏步 面右|1F6B6-1F3FB-200D-27A1-FE0F:🚶🏻‍➡️ 1F6B6-1F3FC-200D-27A1-FE0F:🚶🏼‍➡️ 1F6B6-1F3FD-200D-27A1-FE0F:🚶🏽‍➡️ 1F6B6-1F3FE-200D-27A1-FE0F:🚶🏾‍➡️ 1F6B6-1F3FF-200D-27A1-FE0F:🚶🏿‍➡️
1|1F6B6-200D-2640-FE0F-200D-27A1-FE0F|🚶‍♀️‍➡️|woman walking: facing right|女行人：面右|amble facing gait hike man pace pedestrian right stride stroll walk walking woman woman walking: facing right 女行人：面右 女行人 漫步 走路 路人 閒逛 面右|1F6B6-1F3FB-200D-2640-FE0F-200D-27A1-FE0F:🚶🏻‍♀️‍➡️ 1F6B6-1F3FC-200D-2640-FE0F-200D-27A1-FE0F:🚶🏼‍♀️‍➡️ 1F6B6-1F3FD-200D-2640-FE0F-200D-27A1-FE0F:🚶🏽‍♀️‍➡️ 1F6B6-1F3FE-200D-2640-FE0F-200D-27A1-FE0F:🚶🏾‍♀️‍➡️ 1F6B6-1F3FF-200D-2640-FE0F-200D-27A1-FE0F:🚶🏿‍♀️‍➡️
1|1F6B6-200D-2642-FE0F-200D-27A1-FE0F|🚶‍♂️‍➡️|man walking: facing right|男行人：面右|amble facing gait hike man pace pedestrian right stride stroll walk walking man walking: facing right 男行人：面右 男行人 走路 路人 面右|1F6B6-1F3FB-200D-2642-FE0F-200D-27A1-FE0F:🚶🏻‍♂️‍➡️ 1F6B6-1F3FC-200D-2642-FE0F-200D-27A1-FE0F:🚶🏼‍♂️‍➡️ 1F6B6-1F3FD-200D-2642-FE0F-200D-27A1-FE0F:🚶🏽‍♂️‍➡️ 1F6B6-1F3FE-200D-2642-FE0F-200D-27A1-FE0F:🚶🏾‍♂️‍➡️ 1F6B6-1F3FF-200D-2642-FE0F-200D-27A1-FE0F:🚶🏿‍♂️‍➡️
1|1F9CD|🧍|person standing|站著的人|person stand standing person standing standing_person 站著的人 人 站立 站著|1F9CD-1F3FB:🧍🏻 1F9CD-1F3FC:🧍🏼 1F9CD-1F3FD:🧍🏽 1F9CD-1F3FE:🧍🏾 1F9CD-1F3FF:🧍🏿
1|1F9CD-200D-2642-FE0F|🧍‍♂️|man standing|站著的男子|man stand standing man standing standing_man 站著的男子 男 站立|1F9CD-1F3FB-200D-2642-FE0F:🧍🏻‍♂️ 1F9CD-1F3FC-200D-2642-FE0F:🧍🏼‍♂️ 1F9CD-1F3FD-200D-2642-FE0F:🧍🏽‍♂️ 1F9CD-1F3FE-200D-2642-FE0F:🧍🏾‍♂️ 1F9CD-1F3FF-200D-2642-FE0F:🧍🏿‍♂️
1|1F9CD-200D-2640-FE0F|🧍‍♀️|woman standing|站著的女子|stand standing woman woman standing standing_woman 站著的女子 女 站立|1F9CD-1F3FB-200D-2640-FE0F:🧍🏻‍♀️ 1F9CD-1F3FC-200D-2640-FE0F:🧍🏼‍♀️ 1F9CD-1F3FD-200D-2640-FE0F:🧍🏽‍♀️ 1F9CD-1F3FE-200D-2640-FE0F:🧍🏾‍♀️ 1F9CD-1F3FF-200D-2640-FE0F:🧍🏿‍♀️
1|1F9CE|🧎|person kneeling|跪著的人|kneel kneeling knees person person kneeling kneeling_person 跪著的人 人 跪 跪下|1F9CE-1F3FB:🧎🏻 1F9CE-1F3FC:🧎🏼 1F9CE-1F3FD:🧎🏽 1F9CE-1F3FE:🧎🏾 1F9CE-1F3FF:🧎🏿
1|1F9CE-200D-2642-FE0F|🧎‍♂️|man kneeling|跪著的男子|kneel kneeling knees man man kneeling kneeling_man 跪著的男子 男 跪|1F9CE-1F3FB-200D-2642-FE0F:🧎🏻‍♂️ 1F9CE-1F3FC-200D-2642-FE0F:🧎🏼‍♂️ 1F9CE-1F3FD-200D-2642-FE0F:🧎🏽‍♂️ 1F9CE-1F3FE-200D-2642-FE0F:🧎🏾‍♂️ 1F9CE-1F3FF-200D-2642-FE0F:🧎🏿‍♂️
1|1F9CE-200D-2640-FE0F|🧎‍♀️|woman kneeling|跪著的女子|kneel kneeling knees woman woman kneeling kneeling_woman 跪著的女子 女 跪|1F9CE-1F3FB-200D-2640-FE0F:🧎🏻‍♀️ 1F9CE-1F3FC-200D-2640-FE0F:🧎🏼‍♀️ 1F9CE-1F3FD-200D-2640-FE0F:🧎🏽‍♀️ 1F9CE-1F3FE-200D-2640-FE0F:🧎🏾‍♀️ 1F9CE-1F3FF-200D-2640-FE0F:🧎🏿‍♀️
1|1F9CE-200D-27A1-FE0F|🧎‍➡️|person kneeling: facing right|跪著的人：面右|facing kneel kneeling knees person right person kneeling: facing right 跪著的人：面右 人 跪 跪下 跪著的人 面右|1F9CE-1F3FB-200D-27A1-FE0F:🧎🏻‍➡️ 1F9CE-1F3FC-200D-27A1-FE0F:🧎🏼‍➡️ 1F9CE-1F3FD-200D-27A1-FE0F:🧎🏽‍➡️ 1F9CE-1F3FE-200D-27A1-FE0F:🧎🏾‍➡️ 1F9CE-1F3FF-200D-27A1-FE0F:🧎🏿‍➡️
1|1F9CE-200D-2640-FE0F-200D-27A1-FE0F|🧎‍♀️‍➡️|woman kneeling: facing right|跪著的女子：面右|facing kneel kneeling knees right woman woman kneeling: facing right 跪著的女子：面右 女 跪 跪著的女子 面右|1F9CE-1F3FB-200D-2640-FE0F-200D-27A1-FE0F:🧎🏻‍♀️‍➡️ 1F9CE-1F3FC-200D-2640-FE0F-200D-27A1-FE0F:🧎🏼‍♀️‍➡️ 1F9CE-1F3FD-200D-2640-FE0F-200D-27A1-FE0F:🧎🏽‍♀️‍➡️ 1F9CE-1F3FE-200D-2640-FE0F-200D-27A1-FE0F:🧎🏾‍♀️‍➡️ 1F9CE-1F3FF-200D-2640-FE0F-200D-27A1-FE0F:🧎🏿‍♀️‍➡️
1|1F9CE-200D-2642-FE0F-200D-27A1-FE0F|🧎‍♂️‍➡️|man kneeling: facing right|跪著的男子：面右|facing kneel kneeling knees man right man kneeling: facing right 跪著的男子：面右 男 跪 跪著的男子 面右|1F9CE-1F3FB-200D-2642-FE0F-200D-27A1-FE0F:🧎🏻‍♂️‍➡️ 1F9CE-1F3FC-200D-2642-FE0F-200D-27A1-FE0F:🧎🏼‍♂️‍➡️ 1F9CE-1F3FD-200D-2642-FE0F-200D-27A1-FE0F:🧎🏽‍♂️‍➡️ 1F9CE-1F3FE-200D-2642-FE0F-200D-27A1-FE0F:🧎🏾‍♂️‍➡️ 1F9CE-1F3FF-200D-2642-FE0F-200D-27A1-FE0F:🧎🏿‍♂️‍➡️
1|1F9D1-200D-1F9AF|🧑‍🦯|person with white cane|拿導盲手杖的人|accessibility blind cane person probing white person with white cane person_with_probing_cane 拿導盲手杖的人 盲人 行動不便|1F9D1-1F3FB-200D-1F9AF:🧑🏻‍🦯 1F9D1-1F3FC-200D-1F9AF:🧑🏼‍🦯 1F9D1-1F3FD-200D-1F9AF:🧑🏽‍🦯 1F9D1-1F3FE-200D-1F9AF:🧑🏾‍🦯 1F9D1-1F3FF-200D-1F9AF:🧑🏿‍🦯
1|1F9D1-200D-1F9AF-200D-27A1-FE0F|🧑‍🦯‍➡️|person with white cane: facing right|拿導盲手杖的人：面右|accessibility blind cane facing person probing right white person with white cane: facing right 拿導盲手杖的人：面右 拿導盲手杖的人 盲人 行動不便 面右|1F9D1-1F3FB-200D-1F9AF-200D-27A1-FE0F:🧑🏻‍🦯‍➡️ 1F9D1-1F3FC-200D-1F9AF-200D-27A1-FE0F:🧑🏼‍🦯‍➡️ 1F9D1-1F3FD-200D-1F9AF-200D-27A1-FE0F:🧑🏽‍🦯‍➡️ 1F9D1-1F3FE-200D-1F9AF-200D-27A1-FE0F:🧑🏾‍🦯‍➡️ 1F9D1-1F3FF-200D-1F9AF-200D-27A1-FE0F:🧑🏿‍🦯‍➡️
1|1F468-200D-1F9AF|👨‍🦯|man with white cane|拿導盲手杖的男子|accessibility blind cane man probing white man with white cane man_with_probing_cane 拿導盲手杖的男子 人 男 男子 盲人 行動不便|1F468-1F3FB-200D-1F9AF:👨🏻‍🦯 1F468-1F3FC-200D-1F9AF:👨🏼‍🦯 1F468-1F3FD-200D-1F9AF:👨🏽‍🦯 1F468-1F3FE-200D-1F9AF:👨🏾‍🦯 1F468-1F3FF-200D-1F9AF:👨🏿‍🦯
1|1F468-200D-1F9AF-200D-27A1-FE0F|👨‍🦯‍➡️|man with white cane: facing right|拿導盲手杖的男子：面右|accessibility blind cane facing man probing right white man with white cane: facing right 拿導盲手杖的男子：面右 人 拿導盲手杖的男子 男 男子 盲人 行動不便 面右|1F468-1F3FB-200D-1F9AF-200D-27A1-FE0F:👨🏻‍🦯‍➡️ 1F468-1F3FC-200D-1F9AF-200D-27A1-FE0F:👨🏼‍🦯‍➡️ 1F468-1F3FD-200D-1F9AF-200D-27A1-FE0F:👨🏽‍🦯‍➡️ 1F468-1F3FE-200D-1F9AF-200D-27A1-FE0F:👨🏾‍🦯‍➡️ 1F468-1F3FF-200D-1F9AF-200D-27A1-FE0F:👨🏿‍🦯‍➡️
1|1F469-200D-1F9AF|👩‍🦯|woman with white cane|拿導盲手杖的女子|accessibility blind cane probing white woman woman with white cane woman_with_probing_cane 拿導盲手杖的女子 人 女 女子 盲人 行動不便|1F469-1F3FB-200D-1F9AF:👩🏻‍🦯 1F469-1F3FC-200D-1F9AF:👩🏼‍🦯 1F469-1F3FD-200D-1F9AF:👩🏽‍🦯 1F469-1F3FE-200D-1F9AF:👩🏾‍🦯 1F469-1F3FF-200D-1F9AF:👩🏿‍🦯
1|1F469-200D-1F9AF-200D-27A1-FE0F|👩‍🦯‍➡️|woman with white cane: facing right|拿導盲手杖的女子：面右|accessibility blind cane facing probing right white woman woman with white cane: facing right 拿導盲手杖的女子：面右 人 女 女子 拿導盲手杖的女子 盲人 行動不便 面右|1F469-1F3FB-200D-1F9AF-200D-27A1-FE0F:👩🏻‍🦯‍➡️ 1F469-1F3FC-200D-1F9AF-200D-27A1-FE0F:👩🏼‍🦯‍➡️ 1F469-1F3FD-200D-1F9AF-200D-27A1-FE0F:👩🏽‍🦯‍➡️ 1F469-1F3FE-200D-1F9AF-200D-27A1-FE0F:👩🏾‍🦯‍➡️ 1F469-1F3FF-200D-1F9AF-200D-27A1-FE0F:👩🏿‍🦯‍➡️
1|1F9D1-200D-1F9BC|🧑‍🦼|person in motorized wheelchair|坐電動輪椅的人|accessibility motorized person wheelchair person in motorized wheelchair person_in_motorized_wheelchair 坐電動輪椅的人 行動不便 輪椅|1F9D1-1F3FB-200D-1F9BC:🧑🏻‍🦼 1F9D1-1F3FC-200D-1F9BC:🧑🏼‍🦼 1F9D1-1F3FD-200D-1F9BC:🧑🏽‍🦼 1F9D1-1F3FE-200D-1F9BC:🧑🏾‍🦼 1F9D1-1F3FF-200D-1F9BC:🧑🏿‍🦼
1|1F9D1-200D-1F9BC-200D-27A1-FE0F|🧑‍🦼‍➡️|person in motorized wheelchair: facing right|坐電動輪椅的人：面右|accessibility facing motorized person right wheelchair person in motorized wheelchair: facing right 坐電動輪椅的人：面右 坐電動輪椅的人 行動不便 輪椅 面右|1F9D1-1F3FB-200D-1F9BC-200D-27A1-FE0F:🧑🏻‍🦼‍➡️ 1F9D1-1F3FC-200D-1F9BC-200D-27A1-FE0F:🧑🏼‍🦼‍➡️ 1F9D1-1F3FD-200D-1F9BC-200D-27A1-FE0F:🧑🏽‍🦼‍➡️ 1F9D1-1F3FE-200D-1F9BC-200D-27A1-FE0F:🧑🏾‍🦼‍➡️ 1F9D1-1F3FF-200D-1F9BC-200D-27A1-FE0F:🧑🏿‍🦼‍➡️
1|1F468-200D-1F9BC|👨‍🦼|man in motorized wheelchair|坐電動輪椅的男子|accessibility man motorized wheelchair man in motorized wheelchair man_in_motorized_wheelchair 坐電動輪椅的男子 人 男 男子 行動不便 輪椅 電動輪椅|1F468-1F3FB-200D-1F9BC:👨🏻‍🦼 1F468-1F3FC-200D-1F9BC:👨🏼‍🦼 1F468-1F3FD-200D-1F9BC:👨🏽‍🦼 1F468-1F3FE-200D-1F9BC:👨🏾‍🦼 1F468-1F3FF-200D-1F9BC:👨🏿‍🦼
1|1F468-200D-1F9BC-200D-27A1-FE0F|👨‍🦼‍➡️|man in motorized wheelchair: facing right|坐電動輪椅的男子：面右|accessibility facing man motorized right wheelchair man in motorized wheelchair: facing right 坐電動輪椅的男子：面右 人 坐電動輪椅的男子 男 男子 行動不便 輪椅 電動輪椅 面右|1F468-1F3FB-200D-1F9BC-200D-27A1-FE0F:👨🏻‍🦼‍➡️ 1F468-1F3FC-200D-1F9BC-200D-27A1-FE0F:👨🏼‍🦼‍➡️ 1F468-1F3FD-200D-1F9BC-200D-27A1-FE0F:👨🏽‍🦼‍➡️ 1F468-1F3FE-200D-1F9BC-200D-27A1-FE0F:👨🏾‍🦼‍➡️ 1F468-1F3FF-200D-1F9BC-200D-27A1-FE0F:👨🏿‍🦼‍➡️
1|1F469-200D-1F9BC|👩‍🦼|woman in motorized wheelchair|坐電動輪椅的女子|accessibility motorized wheelchair woman woman in motorized wheelchair woman_in_motorized_wheelchair 坐電動輪椅的女子 人 女 女子 行動不便 輪椅 電動輪椅|1F469-1F3FB-200D-1F9BC:👩🏻‍🦼 1F469-1F3FC-200D-1F9BC:👩🏼‍🦼 1F469-1F3FD-200D-1F9BC:👩🏽‍🦼 1F469-1F3FE-200D-1F9BC:👩🏾‍🦼 1F469-1F3FF-200D-1F9BC:👩🏿‍🦼
1|1F469-200D-1F9BC-200D-27A1-FE0F|👩‍🦼‍➡️|woman in motorized wheelchair: facing right|坐電動輪椅的女子：面右|accessibility facing motorized right wheelchair woman woman in motorized wheelchair: facing right 坐電動輪椅的女子：面右 人 坐電動輪椅的女子 女 女子 行動不便 輪椅 電動輪椅 面右|1F469-1F3FB-200D-1F9BC-200D-27A1-FE0F:👩🏻‍🦼‍➡️ 1F469-1F3FC-200D-1F9BC-200D-27A1-FE0F:👩🏼‍🦼‍➡️ 1F469-1F3FD-200D-1F9BC-200D-27A1-FE0F:👩🏽‍🦼‍➡️ 1F469-1F3FE-200D-1F9BC-200D-27A1-FE0F:👩🏾‍🦼‍➡️ 1F469-1F3FF-200D-1F9BC-200D-27A1-FE0F:👩🏿‍🦼‍➡️
"""

private const val EMOJI_ROWS_3 = """
1|1F9D1-200D-1F9BD|🧑‍🦽|person in manual wheelchair|坐輪椅的人|accessibility manual person wheelchair person in manual wheelchair person_in_manual_wheelchair 坐輪椅的人 行動不便 輪椅|1F9D1-1F3FB-200D-1F9BD:🧑🏻‍🦽 1F9D1-1F3FC-200D-1F9BD:🧑🏼‍🦽 1F9D1-1F3FD-200D-1F9BD:🧑🏽‍🦽 1F9D1-1F3FE-200D-1F9BD:🧑🏾‍🦽 1F9D1-1F3FF-200D-1F9BD:🧑🏿‍🦽
1|1F9D1-200D-1F9BD-200D-27A1-FE0F|🧑‍🦽‍➡️|person in manual wheelchair: facing right|坐輪椅的人：面右|accessibility facing manual person right wheelchair person in manual wheelchair: facing right 坐輪椅的人：面右 坐輪椅的人 行動不便 輪椅 面右|1F9D1-1F3FB-200D-1F9BD-200D-27A1-FE0F:🧑🏻‍🦽‍➡️ 1F9D1-1F3FC-200D-1F9BD-200D-27A1-FE0F:🧑🏼‍🦽‍➡️ 1F9D1-1F3FD-200D-1F9BD-200D-27A1-FE0F:🧑🏽‍🦽‍➡️ 1F9D1-1F3FE-200D-1F9BD-200D-27A1-FE0F:🧑🏾‍🦽‍➡️ 1F9D1-1F3FF-200D-1F9BD-200D-27A1-FE0F:🧑🏿‍🦽‍➡️
1|1F468-200D-1F9BD|👨‍🦽|man in manual wheelchair|坐輪椅的男子|accessibility man manual wheelchair man in manual wheelchair man_in_manual_wheelchair 坐輪椅的男子 人 男 男子 行動不便 輪椅|1F468-1F3FB-200D-1F9BD:👨🏻‍🦽 1F468-1F3FC-200D-1F9BD:👨🏼‍🦽 1F468-1F3FD-200D-1F9BD:👨🏽‍🦽 1F468-1F3FE-200D-1F9BD:👨🏾‍🦽 1F468-1F3FF-200D-1F9BD:👨🏿‍🦽
1|1F468-200D-1F9BD-200D-27A1-FE0F|👨‍🦽‍➡️|man in manual wheelchair: facing right|坐輪椅的男子：面右|accessibility facing man manual right wheelchair man in manual wheelchair: facing right 坐輪椅的男子：面右 人 坐輪椅的男子 男 男子 行動不便 輪椅 面右|1F468-1F3FB-200D-1F9BD-200D-27A1-FE0F:👨🏻‍🦽‍➡️ 1F468-1F3FC-200D-1F9BD-200D-27A1-FE0F:👨🏼‍🦽‍➡️ 1F468-1F3FD-200D-1F9BD-200D-27A1-FE0F:👨🏽‍🦽‍➡️ 1F468-1F3FE-200D-1F9BD-200D-27A1-FE0F:👨🏾‍🦽‍➡️ 1F468-1F3FF-200D-1F9BD-200D-27A1-FE0F:👨🏿‍🦽‍➡️
1|1F469-200D-1F9BD|👩‍🦽|woman in manual wheelchair|坐輪椅的女子|accessibility manual wheelchair woman woman in manual wheelchair woman_in_manual_wheelchair 坐輪椅的女子 人 女 女子 行動不便 輪椅|1F469-1F3FB-200D-1F9BD:👩🏻‍🦽 1F469-1F3FC-200D-1F9BD:👩🏼‍🦽 1F469-1F3FD-200D-1F9BD:👩🏽‍🦽 1F469-1F3FE-200D-1F9BD:👩🏾‍🦽 1F469-1F3FF-200D-1F9BD:👩🏿‍🦽
1|1F469-200D-1F9BD-200D-27A1-FE0F|👩‍🦽‍➡️|woman in manual wheelchair: facing right|坐輪椅的女子：面右|accessibility facing manual right wheelchair woman woman in manual wheelchair: facing right 坐輪椅的女子：面右 人 坐輪椅的女子 女 女子 行動不便 輪椅 面右|1F469-1F3FB-200D-1F9BD-200D-27A1-FE0F:👩🏻‍🦽‍➡️ 1F469-1F3FC-200D-1F9BD-200D-27A1-FE0F:👩🏼‍🦽‍➡️ 1F469-1F3FD-200D-1F9BD-200D-27A1-FE0F:👩🏽‍🦽‍➡️ 1F469-1F3FE-200D-1F9BD-200D-27A1-FE0F:👩🏾‍🦽‍➡️ 1F469-1F3FF-200D-1F9BD-200D-27A1-FE0F:👩🏿‍🦽‍➡️
1|1F3C3|🏃|person running|跑者|fast hurry marathon move person quick race racing run rush speed person running runner running 跑者 人物 快跑 移動 衝 跑步 跑過來 跑馬拉松 速度|1F3C3-1F3FB:🏃🏻 1F3C3-1F3FC:🏃🏼 1F3C3-1F3FD:🏃🏽 1F3C3-1F3FE:🏃🏾 1F3C3-1F3FF:🏃🏿
1|1F3C3-200D-2642-FE0F|🏃‍♂️|man running|男跑者|fast hurry man marathon move quick race racing run rush speed man running running_man 男跑者 男 跑 馬拉松|1F3C3-1F3FB-200D-2642-FE0F:🏃🏻‍♂️ 1F3C3-1F3FC-200D-2642-FE0F:🏃🏼‍♂️ 1F3C3-1F3FD-200D-2642-FE0F:🏃🏽‍♂️ 1F3C3-1F3FE-200D-2642-FE0F:🏃🏾‍♂️ 1F3C3-1F3FF-200D-2642-FE0F:🏃🏿‍♂️
1|1F3C3-200D-2640-FE0F|🏃‍♀️|woman running|女跑者|fast hurry marathon move quick race racing run rush speed woman woman running running_woman 女跑者 女 跑 馬拉松|1F3C3-1F3FB-200D-2640-FE0F:🏃🏻‍♀️ 1F3C3-1F3FC-200D-2640-FE0F:🏃🏼‍♀️ 1F3C3-1F3FD-200D-2640-FE0F:🏃🏽‍♀️ 1F3C3-1F3FE-200D-2640-FE0F:🏃🏾‍♀️ 1F3C3-1F3FF-200D-2640-FE0F:🏃🏿‍♀️
1|1F3C3-200D-27A1-FE0F|🏃‍➡️|person running: facing right|跑者：面右|facing fast hurry marathon move person quick race racing right run rush speed person running: facing right 跑者：面右 人物 快跑 移動 衝 跑步 跑者 跑過來 跑馬拉松 速度 面右|1F3C3-1F3FB-200D-27A1-FE0F:🏃🏻‍➡️ 1F3C3-1F3FC-200D-27A1-FE0F:🏃🏼‍➡️ 1F3C3-1F3FD-200D-27A1-FE0F:🏃🏽‍➡️ 1F3C3-1F3FE-200D-27A1-FE0F:🏃🏾‍➡️ 1F3C3-1F3FF-200D-27A1-FE0F:🏃🏿‍➡️
1|1F3C3-200D-2640-FE0F-200D-27A1-FE0F|🏃‍♀️‍➡️|woman running: facing right|女跑者：面右|facing fast hurry marathon move quick race racing right run rush speed woman woman running: facing right 女跑者：面右 女 女跑者 跑 面右 馬拉松|1F3C3-1F3FB-200D-2640-FE0F-200D-27A1-FE0F:🏃🏻‍♀️‍➡️ 1F3C3-1F3FC-200D-2640-FE0F-200D-27A1-FE0F:🏃🏼‍♀️‍➡️ 1F3C3-1F3FD-200D-2640-FE0F-200D-27A1-FE0F:🏃🏽‍♀️‍➡️ 1F3C3-1F3FE-200D-2640-FE0F-200D-27A1-FE0F:🏃🏾‍♀️‍➡️ 1F3C3-1F3FF-200D-2640-FE0F-200D-27A1-FE0F:🏃🏿‍♀️‍➡️
1|1F3C3-200D-2642-FE0F-200D-27A1-FE0F|🏃‍♂️‍➡️|man running: facing right|男跑者：面右|facing fast hurry man marathon move quick race racing right run rush speed man running: facing right 男跑者：面右 男 男跑者 跑 面右 馬拉松|1F3C3-1F3FB-200D-2642-FE0F-200D-27A1-FE0F:🏃🏻‍♂️‍➡️ 1F3C3-1F3FC-200D-2642-FE0F-200D-27A1-FE0F:🏃🏼‍♂️‍➡️ 1F3C3-1F3FD-200D-2642-FE0F-200D-27A1-FE0F:🏃🏽‍♂️‍➡️ 1F3C3-1F3FE-200D-2642-FE0F-200D-27A1-FE0F:🏃🏾‍♂️‍➡️ 1F3C3-1F3FF-200D-2642-FE0F-200D-27A1-FE0F:🏃🏿‍♂️‍➡️
1|1F9D1-200D-1FA70|🧑‍🩰|ballet dancer|芭蕾舞者|ballet dancer ballet dancer 芭蕾舞者 舞者 芭蕾|1F9D1-1F3FB-200D-1FA70:🧑🏻‍🩰 1F9D1-1F3FC-200D-1FA70:🧑🏼‍🩰 1F9D1-1F3FD-200D-1FA70:🧑🏽‍🩰 1F9D1-1F3FE-200D-1FA70:🧑🏾‍🩰 1F9D1-1F3FF-200D-1FA70:🧑🏿‍🩰
1|1F483|💃|woman dancing|舞者|dance dancer dancing elegant festive flair flamenco groove let’s salsa tango woman woman dancing woman_dancing 舞者 佛羅明哥 女舞者 探戈 跳舞 跳舞去|1F483-1F3FB:💃🏻 1F483-1F3FC:💃🏼 1F483-1F3FD:💃🏽 1F483-1F3FE:💃🏾 1F483-1F3FF:💃🏿
1|1F57A|🕺|man dancing|男人跳著舞|dance dancer dancing elegant festive flair flamenco groove let’s man salsa tango man dancing man_dancing 男人跳著舞 佛朗明哥舞 去跳舞 探戈 男 男舞者 舞 莎莎舞 跳 跳舞|1F57A-1F3FB:🕺🏻 1F57A-1F3FC:🕺🏼 1F57A-1F3FD:🕺🏽 1F57A-1F3FE:🕺🏾 1F57A-1F3FF:🕺🏿
1|1F574|🕴️|person in suit levitating|穿西裝的人|business levitating person suit person in suit levitating business_suit_levitating 穿西裝的人 穿著正式 西裝|1F574-1F3FB:🕴🏻 1F574-1F3FC:🕴🏼 1F574-1F3FD:🕴🏽 1F574-1F3FE:🕴🏾 1F574-1F3FF:🕴🏿
1|1F46F|👯|people with bunny ears|兔女郎|bestie bff bunny counterpart dancer double ear identical pair party partying people soulmate twin twinsies people with bunny ears dancers 兔女郎 兔耳 死黨 走趴 跳舞 閨蜜 雙胞胎 高衩|1F46F-1F3FB:👯🏻 1F46F-1F3FC:👯🏼 1F46F-1F3FD:👯🏽 1F46F-1F3FE:👯🏾 1F46F-1F3FF:👯🏿 1F9D1-1F3FB-200D-1F430-200D-1F9D1-1F3FC:🧑🏻‍🐰‍🧑🏼 1F9D1-1F3FB-200D-1F430-200D-1F9D1-1F3FD:🧑🏻‍🐰‍🧑🏽 1F9D1-1F3FB-200D-1F430-200D-1F9D1-1F3FE:🧑🏻‍🐰‍🧑🏾 1F9D1-1F3FB-200D-1F430-200D-1F9D1-1F3FF:🧑🏻‍🐰‍🧑🏿 1F9D1-1F3FC-200D-1F430-200D-1F9D1-1F3FB:🧑🏼‍🐰‍🧑🏻 1F9D1-1F3FC-200D-1F430-200D-1F9D1-1F3FD:🧑🏼‍🐰‍🧑🏽 1F9D1-1F3FC-200D-1F430-200D-1F9D1-1F3FE:🧑🏼‍🐰‍🧑🏾 1F9D1-1F3FC-200D-1F430-200D-1F9D1-1F3FF:🧑🏼‍🐰‍🧑🏿 1F9D1-1F3FD-200D-1F430-200D-1F9D1-1F3FB:🧑🏽‍🐰‍🧑🏻 1F9D1-1F3FD-200D-1F430-200D-1F9D1-1F3FC:🧑🏽‍🐰‍🧑🏼 1F9D1-1F3FD-200D-1F430-200D-1F9D1-1F3FE:🧑🏽‍🐰‍🧑🏾 1F9D1-1F3FD-200D-1F430-200D-1F9D1-1F3FF:🧑🏽‍🐰‍🧑🏿 1F9D1-1F3FE-200D-1F430-200D-1F9D1-1F3FB:🧑🏾‍🐰‍🧑🏻 1F9D1-1F3FE-200D-1F430-200D-1F9D1-1F3FC:🧑🏾‍🐰‍🧑🏼 1F9D1-1F3FE-200D-1F430-200D-1F9D1-1F3FD:🧑🏾‍🐰‍🧑🏽 1F9D1-1F3FE-200D-1F430-200D-1F9D1-1F3FF:🧑🏾‍🐰‍🧑🏿 1F9D1-1F3FF-200D-1F430-200D-1F9D1-1F3FB:🧑🏿‍🐰‍🧑🏻 1F9D1-1F3FF-200D-1F430-200D-1F9D1-1F3FC:🧑🏿‍🐰‍🧑🏼 1F9D1-1F3FF-200D-1F430-200D-1F9D1-1F3FD:🧑🏿‍🐰‍🧑🏽 1F9D1-1F3FF-200D-1F430-200D-1F9D1-1F3FE:🧑🏿‍🐰‍🧑🏾
1|1F46F-200D-2642-FE0F|👯‍♂️|men with bunny ears|雙人兔男郎|bestie bff bunny counterpart dancer double ear identical men pair party partying people soulmate twin twinsies men with bunny ears dancing_men 雙人兔男郎 兔耳 戴兔耳朵 跳舞|1F468-1F3FB-200D-1F430-200D-1F468-1F3FC:👨🏻‍🐰‍👨🏼 1F468-1F3FB-200D-1F430-200D-1F468-1F3FD:👨🏻‍🐰‍👨🏽 1F468-1F3FB-200D-1F430-200D-1F468-1F3FE:👨🏻‍🐰‍👨🏾 1F468-1F3FB-200D-1F430-200D-1F468-1F3FF:👨🏻‍🐰‍👨🏿 1F468-1F3FC-200D-1F430-200D-1F468-1F3FB:👨🏼‍🐰‍👨🏻 1F468-1F3FC-200D-1F430-200D-1F468-1F3FD:👨🏼‍🐰‍👨🏽 1F468-1F3FC-200D-1F430-200D-1F468-1F3FE:👨🏼‍🐰‍👨🏾 1F468-1F3FC-200D-1F430-200D-1F468-1F3FF:👨🏼‍🐰‍👨🏿 1F468-1F3FD-200D-1F430-200D-1F468-1F3FB:👨🏽‍🐰‍👨🏻 1F468-1F3FD-200D-1F430-200D-1F468-1F3FC:👨🏽‍🐰‍👨🏼 1F468-1F3FD-200D-1F430-200D-1F468-1F3FE:👨🏽‍🐰‍👨🏾 1F468-1F3FD-200D-1F430-200D-1F468-1F3FF:👨🏽‍🐰‍👨🏿 1F468-1F3FE-200D-1F430-200D-1F468-1F3FB:👨🏾‍🐰‍👨🏻 1F468-1F3FE-200D-1F430-200D-1F468-1F3FC:👨🏾‍🐰‍👨🏼 1F468-1F3FE-200D-1F430-200D-1F468-1F3FD:👨🏾‍🐰‍👨🏽 1F468-1F3FE-200D-1F430-200D-1F468-1F3FF:👨🏾‍🐰‍👨🏿 1F468-1F3FF-200D-1F430-200D-1F468-1F3FB:👨🏿‍🐰‍👨🏻 1F468-1F3FF-200D-1F430-200D-1F468-1F3FC:👨🏿‍🐰‍👨🏼 1F468-1F3FF-200D-1F430-200D-1F468-1F3FD:👨🏿‍🐰‍👨🏽 1F468-1F3FF-200D-1F430-200D-1F468-1F3FE:👨🏿‍🐰‍👨🏾 1F46F-1F3FB-200D-2642-FE0F:👯🏻‍♂️ 1F46F-1F3FC-200D-2642-FE0F:👯🏼‍♂️ 1F46F-1F3FD-200D-2642-FE0F:👯🏽‍♂️ 1F46F-1F3FE-200D-2642-FE0F:👯🏾‍♂️ 1F46F-1F3FF-200D-2642-FE0F:👯🏿‍♂️
1|1F46F-200D-2640-FE0F|👯‍♀️|women with bunny ears|雙人兔女郎|bestie bff bunny counterpart dancer double ear identical pair party partying people soulmate twin twinsies women women with bunny ears dancing_women 雙人兔女郎 兔耳 跳舞|1F469-1F3FB-200D-1F430-200D-1F469-1F3FC:👩🏻‍🐰‍👩🏼 1F469-1F3FB-200D-1F430-200D-1F469-1F3FD:👩🏻‍🐰‍👩🏽 1F469-1F3FB-200D-1F430-200D-1F469-1F3FE:👩🏻‍🐰‍👩🏾 1F469-1F3FB-200D-1F430-200D-1F469-1F3FF:👩🏻‍🐰‍👩🏿 1F469-1F3FC-200D-1F430-200D-1F469-1F3FB:👩🏼‍🐰‍👩🏻 1F469-1F3FC-200D-1F430-200D-1F469-1F3FD:👩🏼‍🐰‍👩🏽 1F469-1F3FC-200D-1F430-200D-1F469-1F3FE:👩🏼‍🐰‍👩🏾 1F469-1F3FC-200D-1F430-200D-1F469-1F3FF:👩🏼‍🐰‍👩🏿 1F469-1F3FD-200D-1F430-200D-1F469-1F3FB:👩🏽‍🐰‍👩🏻 1F469-1F3FD-200D-1F430-200D-1F469-1F3FC:👩🏽‍🐰‍👩🏼 1F469-1F3FD-200D-1F430-200D-1F469-1F3FE:👩🏽‍🐰‍👩🏾 1F469-1F3FD-200D-1F430-200D-1F469-1F3FF:👩🏽‍🐰‍👩🏿 1F469-1F3FE-200D-1F430-200D-1F469-1F3FB:👩🏾‍🐰‍👩🏻 1F469-1F3FE-200D-1F430-200D-1F469-1F3FC:👩🏾‍🐰‍👩🏼 1F469-1F3FE-200D-1F430-200D-1F469-1F3FD:👩🏾‍🐰‍👩🏽 1F469-1F3FE-200D-1F430-200D-1F469-1F3FF:👩🏾‍🐰‍👩🏿 1F469-1F3FF-200D-1F430-200D-1F469-1F3FB:👩🏿‍🐰‍👩🏻 1F469-1F3FF-200D-1F430-200D-1F469-1F3FC:👩🏿‍🐰‍👩🏼 1F469-1F3FF-200D-1F430-200D-1F469-1F3FD:👩🏿‍🐰‍👩🏽 1F469-1F3FF-200D-1F430-200D-1F469-1F3FE:👩🏿‍🐰‍👩🏾 1F46F-1F3FB-200D-2640-FE0F:👯🏻‍♀️ 1F46F-1F3FC-200D-2640-FE0F:👯🏼‍♀️ 1F46F-1F3FD-200D-2640-FE0F:👯🏽‍♀️ 1F46F-1F3FE-200D-2640-FE0F:👯🏾‍♀️ 1F46F-1F3FF-200D-2640-FE0F:👯🏿‍♀️
1|1F9D6|🧖|person in steamy room|做蒸氣浴的人|day luxurious pamper person relax room sauna spa steam steambath unwind person in steamy room sauna_person 做蒸氣浴的人 土耳其浴 桑拿 蒸汽浴|1F9D6-1F3FB:🧖🏻 1F9D6-1F3FC:🧖🏼 1F9D6-1F3FD:🧖🏽 1F9D6-1F3FE:🧖🏾 1F9D6-1F3FF:🧖🏿
1|1F9D6-200D-2642-FE0F|🧖‍♂️|man in steamy room|做蒸氣浴的男子|day luxurious man pamper relax room sauna spa steam steambath unwind man in steamy room sauna_man 做蒸氣浴的男子 做蒸氣浴的男子|1F9D6-1F3FB-200D-2642-FE0F:🧖🏻‍♂️ 1F9D6-1F3FC-200D-2642-FE0F:🧖🏼‍♂️ 1F9D6-1F3FD-200D-2642-FE0F:🧖🏽‍♂️ 1F9D6-1F3FE-200D-2642-FE0F:🧖🏾‍♂️ 1F9D6-1F3FF-200D-2642-FE0F:🧖🏿‍♂️
1|1F9D6-200D-2640-FE0F|🧖‍♀️|woman in steamy room|做蒸氣浴的女子|day luxurious pamper relax room sauna spa steam steambath unwind woman woman in steamy room sauna_woman 做蒸氣浴的女子 做蒸氣浴的女子|1F9D6-1F3FB-200D-2640-FE0F:🧖🏻‍♀️ 1F9D6-1F3FC-200D-2640-FE0F:🧖🏼‍♀️ 1F9D6-1F3FD-200D-2640-FE0F:🧖🏽‍♀️ 1F9D6-1F3FE-200D-2640-FE0F:🧖🏾‍♀️ 1F9D6-1F3FF-200D-2640-FE0F:🧖🏿‍♀️
1|1F9D7|🧗|person climbing|攀岩的人|climb climber climbing mountain person rock scale up person climbing 攀岩的人 攀岩 攀岩者 攀爬 爬山|1F9D7-1F3FB:🧗🏻 1F9D7-1F3FC:🧗🏼 1F9D7-1F3FD:🧗🏽 1F9D7-1F3FE:🧗🏾 1F9D7-1F3FF:🧗🏿
1|1F9D7-200D-2642-FE0F|🧗‍♂️|man climbing|攀岩男子|climb climber climbing man mountain rock scale up man climbing climbing_man 攀岩男子 攀岩男子|1F9D7-1F3FB-200D-2642-FE0F:🧗🏻‍♂️ 1F9D7-1F3FC-200D-2642-FE0F:🧗🏼‍♂️ 1F9D7-1F3FD-200D-2642-FE0F:🧗🏽‍♂️ 1F9D7-1F3FE-200D-2642-FE0F:🧗🏾‍♂️ 1F9D7-1F3FF-200D-2642-FE0F:🧗🏿‍♂️
1|1F9D7-200D-2640-FE0F|🧗‍♀️|woman climbing|攀岩女子|climb climber climbing mountain rock scale up woman woman climbing climbing_woman 攀岩女子 攀岩女子|1F9D7-1F3FB-200D-2640-FE0F:🧗🏻‍♀️ 1F9D7-1F3FC-200D-2640-FE0F:🧗🏼‍♀️ 1F9D7-1F3FD-200D-2640-FE0F:🧗🏽‍♀️ 1F9D7-1F3FE-200D-2640-FE0F:🧗🏾‍♀️ 1F9D7-1F3FF-200D-2640-FE0F:🧗🏿‍♀️
1|1F93A|🤺|person fencing|西洋劍|fencer fencing person sword person fencing person_fencing 西洋劍 擊劍 運動|
1|1F3C7|🏇|horse racing|賽馬|horse jockey racehorse racing riding sport horse racing horse_racing 賽馬 馬 騎馬|1F3C7-1F3FB:🏇🏻 1F3C7-1F3FC:🏇🏼 1F3C7-1F3FD:🏇🏽 1F3C7-1F3FE:🏇🏾 1F3C7-1F3FF:🏇🏿
1|26F7|⛷️|skier|滑雪者|ski snow skier 滑雪者 滑雪|
1|1F3C2|🏂️|snowboarder|滑雪板|ski snow snowboard sport snowboarder 滑雪板 滑雪|1F3C2-1F3FB:🏂🏻 1F3C2-1F3FC:🏂🏼 1F3C2-1F3FD:🏂🏽 1F3C2-1F3FE:🏂🏾 1F3C2-1F3FF:🏂🏿
1|1F3CC|🏌️|person golfing|打高爾夫|ball birdie caddy driving golf golfing green person pga putt range tee person golfing 打高爾夫 人物 小白球 揮桿 進洞 運動 高爾夫 高爾夫練習場|1F3CC-1F3FB:🏌🏻 1F3CC-1F3FC:🏌🏼 1F3CC-1F3FD:🏌🏽 1F3CC-1F3FE:🏌🏾 1F3CC-1F3FF:🏌🏿
1|1F3CC-FE0F-200D-2642-FE0F|🏌️‍♂️|man golfing|男生打高爾夫|ball birdie caddy driving golf golfing green man pga putt range tee man golfing golfing_man 男生打高爾夫 男 高爾夫|1F3CC-1F3FB-200D-2642-FE0F:🏌🏻‍♂️ 1F3CC-1F3FC-200D-2642-FE0F:🏌🏼‍♂️ 1F3CC-1F3FD-200D-2642-FE0F:🏌🏽‍♂️ 1F3CC-1F3FE-200D-2642-FE0F:🏌🏾‍♂️ 1F3CC-1F3FF-200D-2642-FE0F:🏌🏿‍♂️
1|1F3CC-FE0F-200D-2640-FE0F|🏌️‍♀️|woman golfing|女生打高爾夫|ball birdie caddy driving golf golfing green pga putt range tee woman woman golfing golfing_woman 女生打高爾夫 女 高爾夫 高爾夫練習場|1F3CC-1F3FB-200D-2640-FE0F:🏌🏻‍♀️ 1F3CC-1F3FC-200D-2640-FE0F:🏌🏼‍♀️ 1F3CC-1F3FD-200D-2640-FE0F:🏌🏽‍♀️ 1F3CC-1F3FE-200D-2640-FE0F:🏌🏾‍♀️ 1F3CC-1F3FF-200D-2640-FE0F:🏌🏿‍♀️
1|1F3C4|🏄️|person surfing|衝浪|beach ocean person sport surf surfer surfing swell waves person surfing 衝浪 人物 水上運動 海 海灘 衝浪板 衝浪者 運動|1F3C4-1F3FB:🏄🏻 1F3C4-1F3FC:🏄🏼 1F3C4-1F3FD:🏄🏽 1F3C4-1F3FE:🏄🏾 1F3C4-1F3FF:🏄🏿
1|1F3C4-200D-2642-FE0F|🏄‍♂️|man surfing|男生衝浪|beach man ocean sport surf surfer surfing swell waves man surfing surfing_man 男生衝浪 男 衝浪|1F3C4-1F3FB-200D-2642-FE0F:🏄🏻‍♂️ 1F3C4-1F3FC-200D-2642-FE0F:🏄🏼‍♂️ 1F3C4-1F3FD-200D-2642-FE0F:🏄🏽‍♂️ 1F3C4-1F3FE-200D-2642-FE0F:🏄🏾‍♂️ 1F3C4-1F3FF-200D-2642-FE0F:🏄🏿‍♂️
1|1F3C4-200D-2640-FE0F|🏄‍♀️|woman surfing|女生衝浪|beach ocean person sport surf surfer surfing swell waves woman surfing surfing_woman 女生衝浪 女 衝浪 衝浪女|1F3C4-1F3FB-200D-2640-FE0F:🏄🏻‍♀️ 1F3C4-1F3FC-200D-2640-FE0F:🏄🏼‍♀️ 1F3C4-1F3FD-200D-2640-FE0F:🏄🏽‍♀️ 1F3C4-1F3FE-200D-2640-FE0F:🏄🏾‍♀️ 1F3C4-1F3FF-200D-2640-FE0F:🏄🏿‍♀️
1|1F6A3|🚣|person rowing boat|划艇|boat canoe cruise fishing lake oar paddle person raft river row rowboat rowing person rowing boat 划艇 人物 划船 小船 碧潭 船槳 運動|1F6A3-1F3FB:🚣🏻 1F6A3-1F3FC:🚣🏼 1F6A3-1F3FD:🚣🏽 1F6A3-1F3FE:🚣🏾 1F6A3-1F3FF:🚣🏿
1|1F6A3-200D-2642-FE0F|🚣‍♂️|man rowing boat|男生划船|boat canoe cruise fishing lake man oar paddle raft river row rowboat rowing man rowing boat rowing_man 男生划船 划船 男|1F6A3-1F3FB-200D-2642-FE0F:🚣🏻‍♂️ 1F6A3-1F3FC-200D-2642-FE0F:🚣🏼‍♂️ 1F6A3-1F3FD-200D-2642-FE0F:🚣🏽‍♂️ 1F6A3-1F3FE-200D-2642-FE0F:🚣🏾‍♂️ 1F6A3-1F3FF-200D-2642-FE0F:🚣🏿‍♂️
1|1F6A3-200D-2640-FE0F|🚣‍♀️|woman rowing boat|女生划船|boat canoe cruise fishing lake oar paddle raft river row rowboat rowing woman woman rowing boat rowing_woman 女生划船 划船 女|1F6A3-1F3FB-200D-2640-FE0F:🚣🏻‍♀️ 1F6A3-1F3FC-200D-2640-FE0F:🚣🏼‍♀️ 1F6A3-1F3FD-200D-2640-FE0F:🚣🏽‍♀️ 1F6A3-1F3FE-200D-2640-FE0F:🚣🏾‍♀️ 1F6A3-1F3FF-200D-2640-FE0F:🚣🏿‍♀️
1|1F3CA|🏊️|person swimming|游泳|freestyle person sport swim swimmer swimming triathlon person swimming 游泳 人物 泳者 游泳選手 自由式 運動 鐵人三項|1F3CA-1F3FB:🏊🏻 1F3CA-1F3FC:🏊🏼 1F3CA-1F3FD:🏊🏽 1F3CA-1F3FE:🏊🏾 1F3CA-1F3FF:🏊🏿
1|1F3CA-200D-2642-FE0F|🏊‍♂️|man swimming|男生游泳|freestyle man sport swim swimmer swimming triathlon man swimming swimming_man 男生游泳 游泳 男|1F3CA-1F3FB-200D-2642-FE0F:🏊🏻‍♂️ 1F3CA-1F3FC-200D-2642-FE0F:🏊🏼‍♂️ 1F3CA-1F3FD-200D-2642-FE0F:🏊🏽‍♂️ 1F3CA-1F3FE-200D-2642-FE0F:🏊🏾‍♂️ 1F3CA-1F3FF-200D-2642-FE0F:🏊🏿‍♂️
1|1F3CA-200D-2640-FE0F|🏊‍♀️|woman swimming|女生游泳|freestyle man sport swim swimmer swimming triathlon woman swimming swimming_woman 女生游泳 女 游泳 運動|1F3CA-1F3FB-200D-2640-FE0F:🏊🏻‍♀️ 1F3CA-1F3FC-200D-2640-FE0F:🏊🏼‍♀️ 1F3CA-1F3FD-200D-2640-FE0F:🏊🏽‍♀️ 1F3CA-1F3FE-200D-2640-FE0F:🏊🏾‍♀️ 1F3CA-1F3FF-200D-2640-FE0F:🏊🏿‍♀️
1|26F9|⛹️|person bouncing ball|打球|athletic ball basketball bouncing championship dribble net person player throw person bouncing ball bouncing_ball_person 打球 人物 比賽 球 籃球 罰球 運動 運動員 運球 選手|26F9-1F3FB:⛹🏻 26F9-1F3FC:⛹🏼 26F9-1F3FD:⛹🏽 26F9-1F3FE:⛹🏾 26F9-1F3FF:⛹🏿
1|26F9-FE0F-200D-2642-FE0F|⛹️‍♂️|man bouncing ball|男生打球|athletic ball basketball bouncing championship dribble man net player throw man bouncing ball basketball_man bouncing_ball_man 男生打球 球 男|26F9-1F3FB-200D-2642-FE0F:⛹🏻‍♂️ 26F9-1F3FC-200D-2642-FE0F:⛹🏼‍♂️ 26F9-1F3FD-200D-2642-FE0F:⛹🏽‍♂️ 26F9-1F3FE-200D-2642-FE0F:⛹🏾‍♂️ 26F9-1F3FF-200D-2642-FE0F:⛹🏿‍♂️
1|26F9-FE0F-200D-2640-FE0F|⛹️‍♀️|woman bouncing ball|女生打球|athletic ball basketball bouncing championship dribble net player throw woman woman bouncing ball basketball_woman bouncing_ball_woman 女生打球 女 打籃球 玩彈力球 球|26F9-1F3FB-200D-2640-FE0F:⛹🏻‍♀️ 26F9-1F3FC-200D-2640-FE0F:⛹🏼‍♀️ 26F9-1F3FD-200D-2640-FE0F:⛹🏽‍♀️ 26F9-1F3FE-200D-2640-FE0F:⛹🏾‍♀️ 26F9-1F3FF-200D-2640-FE0F:⛹🏿‍♀️
1|1F3CB|🏋️|person lifting weights|舉重|barbell bodybuilder deadlift lifter lifting person powerlifting weight weightlifter weights workout person lifting weights weight_lifting 舉重 人物 健身 槓片 槓鈴 運動 重訓 重量訓練|1F3CB-1F3FB:🏋🏻 1F3CB-1F3FC:🏋🏼 1F3CB-1F3FD:🏋🏽 1F3CB-1F3FE:🏋🏾 1F3CB-1F3FF:🏋🏿
1|1F3CB-FE0F-200D-2642-FE0F|🏋️‍♂️|man lifting weights|男生舉重|barbell bodybuilder deadlift lifter lifting man powerlifting weight weightlifter weights workout man lifting weights weight_lifting_man 男生舉重 男 舉重|1F3CB-1F3FB-200D-2642-FE0F:🏋🏻‍♂️ 1F3CB-1F3FC-200D-2642-FE0F:🏋🏼‍♂️ 1F3CB-1F3FD-200D-2642-FE0F:🏋🏽‍♂️ 1F3CB-1F3FE-200D-2642-FE0F:🏋🏾‍♂️ 1F3CB-1F3FF-200D-2642-FE0F:🏋🏿‍♂️
1|1F3CB-FE0F-200D-2640-FE0F|🏋️‍♀️|woman lifting weights|女生舉重|barbell bodybuilder deadlift lifter lifting powerlifting weight weightlifter weights woman workout woman lifting weights weight_lifting_woman 女生舉重 女 舉重 鍛鍊|1F3CB-1F3FB-200D-2640-FE0F:🏋🏻‍♀️ 1F3CB-1F3FC-200D-2640-FE0F:🏋🏼‍♀️ 1F3CB-1F3FD-200D-2640-FE0F:🏋🏽‍♀️ 1F3CB-1F3FE-200D-2640-FE0F:🏋🏾‍♀️ 1F3CB-1F3FF-200D-2640-FE0F:🏋🏿‍♀️
1|1F6B4|🚴|person biking|騎自行車|bicycle bicyclist bike biking cycle cyclist person riding sport person biking 騎自行車 單車 腳踏車 自由車 自行車 自行車騎士 運動 鐵馬 騎登山車 騎車|1F6B4-1F3FB:🚴🏻 1F6B4-1F3FC:🚴🏼 1F6B4-1F3FD:🚴🏽 1F6B4-1F3FE:🚴🏾 1F6B4-1F3FF:🚴🏿
1|1F6B4-200D-2642-FE0F|🚴‍♂️|man biking|男自行車手|bicycle bicyclist bike biking cycle cyclist man riding sport man biking biking_man 男自行車手 男 腳踏車 自行車 騎車|1F6B4-1F3FB-200D-2642-FE0F:🚴🏻‍♂️ 1F6B4-1F3FC-200D-2642-FE0F:🚴🏼‍♂️ 1F6B4-1F3FD-200D-2642-FE0F:🚴🏽‍♂️ 1F6B4-1F3FE-200D-2642-FE0F:🚴🏾‍♂️ 1F6B4-1F3FF-200D-2642-FE0F:🚴🏿‍♂️
1|1F6B4-200D-2640-FE0F|🚴‍♀️|woman biking|女自行車手|bicycle bicyclist bike biking cycle cyclist riding sport woman woman biking biking_woman 女自行車手 女 腳踏車 自行車 騎腳踏車 騎車|1F6B4-1F3FB-200D-2640-FE0F:🚴🏻‍♀️ 1F6B4-1F3FC-200D-2640-FE0F:🚴🏼‍♀️ 1F6B4-1F3FD-200D-2640-FE0F:🚴🏽‍♀️ 1F6B4-1F3FE-200D-2640-FE0F:🚴🏾‍♀️ 1F6B4-1F3FF-200D-2640-FE0F:🚴🏿‍♀️
1|1F6B5|🚵|person mountain biking|騎登山車|bicycle bicyclist bike biking cycle cyclist mountain person riding sport person mountain biking mountain_bicyclist 騎登山車 單車 登山車 腳踏車 自行車 自行車騎士 運動 鐵馬 騎自行車 騎車|1F6B5-1F3FB:🚵🏻 1F6B5-1F3FC:🚵🏼 1F6B5-1F3FD:🚵🏽 1F6B5-1F3FE:🚵🏾 1F6B5-1F3FF:🚵🏿
1|1F6B5-200D-2642-FE0F|🚵‍♂️|man mountain biking|男登山車手|bicycle bicyclist bike biking cycle cyclist man mountain riding sport man mountain biking mountain_biking_man 男登山車手 男 登山車|1F6B5-1F3FB-200D-2642-FE0F:🚵🏻‍♂️ 1F6B5-1F3FC-200D-2642-FE0F:🚵🏼‍♂️ 1F6B5-1F3FD-200D-2642-FE0F:🚵🏽‍♂️ 1F6B5-1F3FE-200D-2642-FE0F:🚵🏾‍♂️ 1F6B5-1F3FF-200D-2642-FE0F:🚵🏿‍♂️
1|1F6B5-200D-2640-FE0F|🚵‍♀️|woman mountain biking|女登山車手|bicycle bicyclist bike biking cycle cyclist mountain riding sport woman woman mountain biking mountain_biking_woman 女登山車手 女 登山車|1F6B5-1F3FB-200D-2640-FE0F:🚵🏻‍♀️ 1F6B5-1F3FC-200D-2640-FE0F:🚵🏼‍♀️ 1F6B5-1F3FD-200D-2640-FE0F:🚵🏽‍♀️ 1F6B5-1F3FE-200D-2640-FE0F:🚵🏾‍♀️ 1F6B5-1F3FF-200D-2640-FE0F:🚵🏿‍♀️
1|1F938|🤸|person cartwheeling|側翻|active cartwheel cartwheeling excited flip gymnastics happy person somersault person cartwheeling 側翻 倒立 活潑 筋斗 興奮 跟斗 運動 運動員 開心 體操|1F938-1F3FB:🤸🏻 1F938-1F3FC:🤸🏼 1F938-1F3FD:🤸🏽 1F938-1F3FE:🤸🏾 1F938-1F3FF:🤸🏿
1|1F938-200D-2642-FE0F|🤸‍♂️|man cartwheeling|男生側翻|active cartwheel cartwheeling excited flip gymnastics happy man somersault man cartwheeling man_cartwheeling 男生側翻 倒立 活潑 筋斗 翻跟斗 興奮 跟斗 運動 運動員 開心 體操|1F938-1F3FB-200D-2642-FE0F:🤸🏻‍♂️ 1F938-1F3FC-200D-2642-FE0F:🤸🏼‍♂️ 1F938-1F3FD-200D-2642-FE0F:🤸🏽‍♂️ 1F938-1F3FE-200D-2642-FE0F:🤸🏾‍♂️ 1F938-1F3FF-200D-2642-FE0F:🤸🏿‍♂️
1|1F938-200D-2640-FE0F|🤸‍♀️|woman cartwheeling|女生側翻|active cartwheel cartwheeling excited flip gymnastics happy somersault woman woman cartwheeling woman_cartwheeling 女生側翻 倒立 活潑 筋斗 翻跟斗 興奮 跟斗 運動 運動員 開心 體操|1F938-1F3FB-200D-2640-FE0F:🤸🏻‍♀️ 1F938-1F3FC-200D-2640-FE0F:🤸🏼‍♀️ 1F938-1F3FD-200D-2640-FE0F:🤸🏽‍♀️ 1F938-1F3FE-200D-2640-FE0F:🤸🏾‍♀️ 1F938-1F3FF-200D-2640-FE0F:🤸🏿‍♀️
1|1F93C|🤼|people wrestling|摔角手|combat duel grapple people ring tournament wrestle wrestling people wrestling 摔角手 單挑 拼了 搏鬥 摔角 比賽 競賽 角力 運動員 釘孤枝|1F93C-1F3FB:🤼🏻 1F93C-1F3FC:🤼🏼 1F93C-1F3FD:🤼🏽 1F93C-1F3FE:🤼🏾 1F93C-1F3FF:🤼🏿 1F9D1-1F3FB-200D-1FAEF-200D-1F9D1-1F3FC:🧑🏻‍🫯‍🧑🏼 1F9D1-1F3FB-200D-1FAEF-200D-1F9D1-1F3FD:🧑🏻‍🫯‍🧑🏽 1F9D1-1F3FB-200D-1FAEF-200D-1F9D1-1F3FE:🧑🏻‍🫯‍🧑🏾 1F9D1-1F3FB-200D-1FAEF-200D-1F9D1-1F3FF:🧑🏻‍🫯‍🧑🏿 1F9D1-1F3FC-200D-1FAEF-200D-1F9D1-1F3FB:🧑🏼‍🫯‍🧑🏻 1F9D1-1F3FC-200D-1FAEF-200D-1F9D1-1F3FD:🧑🏼‍🫯‍🧑🏽 1F9D1-1F3FC-200D-1FAEF-200D-1F9D1-1F3FE:🧑🏼‍🫯‍🧑🏾 1F9D1-1F3FC-200D-1FAEF-200D-1F9D1-1F3FF:🧑🏼‍🫯‍🧑🏿 1F9D1-1F3FD-200D-1FAEF-200D-1F9D1-1F3FB:🧑🏽‍🫯‍🧑🏻 1F9D1-1F3FD-200D-1FAEF-200D-1F9D1-1F3FC:🧑🏽‍🫯‍🧑🏼 1F9D1-1F3FD-200D-1FAEF-200D-1F9D1-1F3FE:🧑🏽‍🫯‍🧑🏾 1F9D1-1F3FD-200D-1FAEF-200D-1F9D1-1F3FF:🧑🏽‍🫯‍🧑🏿 1F9D1-1F3FE-200D-1FAEF-200D-1F9D1-1F3FB:🧑🏾‍🫯‍🧑🏻 1F9D1-1F3FE-200D-1FAEF-200D-1F9D1-1F3FC:🧑🏾‍🫯‍🧑🏼 1F9D1-1F3FE-200D-1FAEF-200D-1F9D1-1F3FD:🧑🏾‍🫯‍🧑🏽 1F9D1-1F3FE-200D-1FAEF-200D-1F9D1-1F3FF:🧑🏾‍🫯‍🧑🏿 1F9D1-1F3FF-200D-1FAEF-200D-1F9D1-1F3FB:🧑🏿‍🫯‍🧑🏻 1F9D1-1F3FF-200D-1FAEF-200D-1F9D1-1F3FC:🧑🏿‍🫯‍🧑🏼 1F9D1-1F3FF-200D-1FAEF-200D-1F9D1-1F3FD:🧑🏿‍🫯‍🧑🏽 1F9D1-1F3FF-200D-1FAEF-200D-1F9D1-1F3FE:🧑🏿‍🫯‍🧑🏾
1|1F93C-200D-2642-FE0F|🤼‍♂️|men wrestling|男子摔角|combat duel grapple men ring tournament wrestle wrestling men wrestling men_wrestling 男子摔角 單挑 拼了 搏鬥 摔角 比賽 競賽 角力 運動員 釘孤枝|1F468-1F3FB-200D-1FAEF-200D-1F468-1F3FC:👨🏻‍🫯‍👨🏼 1F468-1F3FB-200D-1FAEF-200D-1F468-1F3FD:👨🏻‍🫯‍👨🏽 1F468-1F3FB-200D-1FAEF-200D-1F468-1F3FE:👨🏻‍🫯‍👨🏾 1F468-1F3FB-200D-1FAEF-200D-1F468-1F3FF:👨🏻‍🫯‍👨🏿 1F468-1F3FC-200D-1FAEF-200D-1F468-1F3FB:👨🏼‍🫯‍👨🏻 1F468-1F3FC-200D-1FAEF-200D-1F468-1F3FD:👨🏼‍🫯‍👨🏽 1F468-1F3FC-200D-1FAEF-200D-1F468-1F3FE:👨🏼‍🫯‍👨🏾 1F468-1F3FC-200D-1FAEF-200D-1F468-1F3FF:👨🏼‍🫯‍👨🏿 1F468-1F3FD-200D-1FAEF-200D-1F468-1F3FB:👨🏽‍🫯‍👨🏻 1F468-1F3FD-200D-1FAEF-200D-1F468-1F3FC:👨🏽‍🫯‍👨🏼 1F468-1F3FD-200D-1FAEF-200D-1F468-1F3FE:👨🏽‍🫯‍👨🏾 1F468-1F3FD-200D-1FAEF-200D-1F468-1F3FF:👨🏽‍🫯‍👨🏿 1F468-1F3FE-200D-1FAEF-200D-1F468-1F3FB:👨🏾‍🫯‍👨🏻 1F468-1F3FE-200D-1FAEF-200D-1F468-1F3FC:👨🏾‍🫯‍👨🏼 1F468-1F3FE-200D-1FAEF-200D-1F468-1F3FD:👨🏾‍🫯‍👨🏽 1F468-1F3FE-200D-1FAEF-200D-1F468-1F3FF:👨🏾‍🫯‍👨🏿 1F468-1F3FF-200D-1FAEF-200D-1F468-1F3FB:👨🏿‍🫯‍👨🏻 1F468-1F3FF-200D-1FAEF-200D-1F468-1F3FC:👨🏿‍🫯‍👨🏼 1F468-1F3FF-200D-1FAEF-200D-1F468-1F3FD:👨🏿‍🫯‍👨🏽 1F468-1F3FF-200D-1FAEF-200D-1F468-1F3FE:👨🏿‍🫯‍👨🏾 1F93C-1F3FB-200D-2642-FE0F:🤼🏻‍♂️ 1F93C-1F3FC-200D-2642-FE0F:🤼🏼‍♂️ 1F93C-1F3FD-200D-2642-FE0F:🤼🏽‍♂️ 1F93C-1F3FE-200D-2642-FE0F:🤼🏾‍♂️ 1F93C-1F3FF-200D-2642-FE0F:🤼🏿‍♂️
1|1F93C-200D-2640-FE0F|🤼‍♀️|women wrestling|女子摔角|combat duel grapple ring tournament women wrestle wrestling women wrestling women_wrestling 女子摔角 單挑 拼了 搏鬥 摔角 比賽 競賽 角力 運動員 釘孤枝|1F469-1F3FB-200D-1FAEF-200D-1F469-1F3FC:👩🏻‍🫯‍👩🏼 1F469-1F3FB-200D-1FAEF-200D-1F469-1F3FD:👩🏻‍🫯‍👩🏽 1F469-1F3FB-200D-1FAEF-200D-1F469-1F3FE:👩🏻‍🫯‍👩🏾 1F469-1F3FB-200D-1FAEF-200D-1F469-1F3FF:👩🏻‍🫯‍👩🏿 1F469-1F3FC-200D-1FAEF-200D-1F469-1F3FB:👩🏼‍🫯‍👩🏻 1F469-1F3FC-200D-1FAEF-200D-1F469-1F3FD:👩🏼‍🫯‍👩🏽 1F469-1F3FC-200D-1FAEF-200D-1F469-1F3FE:👩🏼‍🫯‍👩🏾 1F469-1F3FC-200D-1FAEF-200D-1F469-1F3FF:👩🏼‍🫯‍👩🏿 1F469-1F3FD-200D-1FAEF-200D-1F469-1F3FB:👩🏽‍🫯‍👩🏻 1F469-1F3FD-200D-1FAEF-200D-1F469-1F3FC:👩🏽‍🫯‍👩🏼 1F469-1F3FD-200D-1FAEF-200D-1F469-1F3FE:👩🏽‍🫯‍👩🏾 1F469-1F3FD-200D-1FAEF-200D-1F469-1F3FF:👩🏽‍🫯‍👩🏿 1F469-1F3FE-200D-1FAEF-200D-1F469-1F3FB:👩🏾‍🫯‍👩🏻 1F469-1F3FE-200D-1FAEF-200D-1F469-1F3FC:👩🏾‍🫯‍👩🏼 1F469-1F3FE-200D-1FAEF-200D-1F469-1F3FD:👩🏾‍🫯‍👩🏽 1F469-1F3FE-200D-1FAEF-200D-1F469-1F3FF:👩🏾‍🫯‍👩🏿 1F469-1F3FF-200D-1FAEF-200D-1F469-1F3FB:👩🏿‍🫯‍👩🏻 1F469-1F3FF-200D-1FAEF-200D-1F469-1F3FC:👩🏿‍🫯‍👩🏼 1F469-1F3FF-200D-1FAEF-200D-1F469-1F3FD:👩🏿‍🫯‍👩🏽 1F469-1F3FF-200D-1FAEF-200D-1F469-1F3FE:👩🏿‍🫯‍👩🏾 1F93C-1F3FB-200D-2640-FE0F:🤼🏻‍♀️ 1F93C-1F3FC-200D-2640-FE0F:🤼🏼‍♀️ 1F93C-1F3FD-200D-2640-FE0F:🤼🏽‍♀️ 1F93C-1F3FE-200D-2640-FE0F:🤼🏾‍♀️ 1F93C-1F3FF-200D-2640-FE0F:🤼🏿‍♀️
1|1F93D|🤽|person playing water polo|水球運動|person playing polo sport swimming water waterpolo person playing water polo water_polo 水球運動 人物 水上 水球 游泳 運動|1F93D-1F3FB:🤽🏻 1F93D-1F3FC:🤽🏼 1F93D-1F3FD:🤽🏽 1F93D-1F3FE:🤽🏾 1F93D-1F3FF:🤽🏿
1|1F93D-200D-2642-FE0F|🤽‍♂️|man playing water polo|男生打水球|man playing polo sport swimming water waterpolo man playing water polo man_playing_water_polo 男生打水球 人物 水球 游泳 男 運動|1F93D-1F3FB-200D-2642-FE0F:🤽🏻‍♂️ 1F93D-1F3FC-200D-2642-FE0F:🤽🏼‍♂️ 1F93D-1F3FD-200D-2642-FE0F:🤽🏽‍♂️ 1F93D-1F3FE-200D-2642-FE0F:🤽🏾‍♂️ 1F93D-1F3FF-200D-2642-FE0F:🤽🏿‍♂️
1|1F93D-200D-2640-FE0F|🤽‍♀️|woman playing water polo|女生打水球|playing polo sport swimming water waterpolo woman woman playing water polo woman_playing_water_polo 女生打水球 人物 女 水球 游泳 運動|1F93D-1F3FB-200D-2640-FE0F:🤽🏻‍♀️ 1F93D-1F3FC-200D-2640-FE0F:🤽🏼‍♀️ 1F93D-1F3FD-200D-2640-FE0F:🤽🏽‍♀️ 1F93D-1F3FE-200D-2640-FE0F:🤽🏾‍♀️ 1F93D-1F3FF-200D-2640-FE0F:🤽🏿‍♀️
1|1F93E|🤾|person playing handball|手球|athletics ball catch chuck handball hurl lob person pitch playing sport throw toss person playing handball handball_person 手球 丟球 人物 扔 投擲 接 球類 運動 運動員|1F93E-1F3FB:🤾🏻 1F93E-1F3FC:🤾🏼 1F93E-1F3FD:🤾🏽 1F93E-1F3FE:🤾🏾 1F93E-1F3FF:🤾🏿
1|1F93E-200D-2642-FE0F|🤾‍♂️|man playing handball|男生打手球|athletics ball catch chuck handball hurl lob man pitch playing sport throw toss man playing handball man_playing_handball 男生打手球 手球 男|1F93E-1F3FB-200D-2642-FE0F:🤾🏻‍♂️ 1F93E-1F3FC-200D-2642-FE0F:🤾🏼‍♂️ 1F93E-1F3FD-200D-2642-FE0F:🤾🏽‍♂️ 1F93E-1F3FE-200D-2642-FE0F:🤾🏾‍♂️ 1F93E-1F3FF-200D-2642-FE0F:🤾🏿‍♂️
1|1F93E-200D-2640-FE0F|🤾‍♀️|woman playing handball|女生打手球|athletics ball catch chuck handball hurl lob pitch playing sport throw toss woman woman playing handball woman_playing_handball 女生打手球 丟球 人物 女 手球 扔 投擲 接 運動 運動員|1F93E-1F3FB-200D-2640-FE0F:🤾🏻‍♀️ 1F93E-1F3FC-200D-2640-FE0F:🤾🏼‍♀️ 1F93E-1F3FD-200D-2640-FE0F:🤾🏽‍♀️ 1F93E-1F3FE-200D-2640-FE0F:🤾🏾‍♀️ 1F93E-1F3FF-200D-2640-FE0F:🤾🏿‍♀️
1|1F939|🤹|person juggling|雜耍|act balance balancing handle juggle juggling manage multitask person skill person juggling juggling_person 雜耍 人物 多才多藝 平衡 平衡感 技藝 特技 表演|1F939-1F3FB:🤹🏻 1F939-1F3FC:🤹🏼 1F939-1F3FD:🤹🏽 1F939-1F3FE:🤹🏾 1F939-1F3FF:🤹🏿
1|1F939-200D-2642-FE0F|🤹‍♂️|man juggling|男生玩雜耍|act balance balancing handle juggle juggling man manage multitask skill man juggling man_juggling 男生玩雜耍 人物 多才多藝 平衡 男 表演 雜耍|1F939-1F3FB-200D-2642-FE0F:🤹🏻‍♂️ 1F939-1F3FC-200D-2642-FE0F:🤹🏼‍♂️ 1F939-1F3FD-200D-2642-FE0F:🤹🏽‍♂️ 1F939-1F3FE-200D-2642-FE0F:🤹🏾‍♂️ 1F939-1F3FF-200D-2642-FE0F:🤹🏿‍♂️
1|1F939-200D-2640-FE0F|🤹‍♀️|woman juggling|女生玩雜耍|act balance balancing handle juggle juggling manage multitask skill woman woman juggling woman_juggling 女生玩雜耍 多工處理 女 雜耍|1F939-1F3FB-200D-2640-FE0F:🤹🏻‍♀️ 1F939-1F3FC-200D-2640-FE0F:🤹🏼‍♀️ 1F939-1F3FD-200D-2640-FE0F:🤹🏽‍♀️ 1F939-1F3FE-200D-2640-FE0F:🤹🏾‍♀️ 1F939-1F3FF-200D-2640-FE0F:🤹🏿‍♀️
1|1F9D8|🧘|person in lotus position|盤坐|cross legged legs lotus meditation peace person position relax serenity yoga yogi zen person in lotus position lotus_position 盤坐 冥想 打坐 放鬆 瑜珈 盤腿 蓮花座 靜坐|1F9D8-1F3FB:🧘🏻 1F9D8-1F3FC:🧘🏼 1F9D8-1F3FD:🧘🏽 1F9D8-1F3FE:🧘🏾 1F9D8-1F3FF:🧘🏿
1|1F9D8-200D-2642-FE0F|🧘‍♂️|man in lotus position|盤坐男子|cross legged legs lotus man meditation peace position relax serenity yoga yogi zen man in lotus position lotus_position_man 盤坐男子 盤坐男子|1F9D8-1F3FB-200D-2642-FE0F:🧘🏻‍♂️ 1F9D8-1F3FC-200D-2642-FE0F:🧘🏼‍♂️ 1F9D8-1F3FD-200D-2642-FE0F:🧘🏽‍♂️ 1F9D8-1F3FE-200D-2642-FE0F:🧘🏾‍♂️ 1F9D8-1F3FF-200D-2642-FE0F:🧘🏿‍♂️
1|1F9D8-200D-2640-FE0F|🧘‍♀️|woman in lotus position|盤坐女子|cross legged legs lotus meditation peace position relax serenity woman yoga yogi zen woman in lotus position lotus_position_woman 盤坐女子 盤坐女子|1F9D8-1F3FB-200D-2640-FE0F:🧘🏻‍♀️ 1F9D8-1F3FC-200D-2640-FE0F:🧘🏼‍♀️ 1F9D8-1F3FD-200D-2640-FE0F:🧘🏽‍♀️ 1F9D8-1F3FE-200D-2640-FE0F:🧘🏾‍♀️ 1F9D8-1F3FF-200D-2640-FE0F:🧘🏿‍♀️
1|1F6C0|🛀|person taking bath|盆浴|bath bathtub person taking tub person taking bath 盆浴 洗澡|1F6C0-1F3FB:🛀🏻 1F6C0-1F3FC:🛀🏼 1F6C0-1F3FD:🛀🏽 1F6C0-1F3FE:🛀🏾 1F6C0-1F3FF:🛀🏿
1|1F6CC|🛌|person in bed|睡覺|bed bedtime good goodnight hotel nap night person sleep tired zzz person in bed sleeping_bed 睡覺 夜裡 打瞌睡 旅館 晚安 睡眠時 要睡著了|1F6CC-1F3FB:🛌🏻 1F6CC-1F3FC:🛌🏼 1F6CC-1F3FD:🛌🏽 1F6CC-1F3FE:🛌🏾 1F6CC-1F3FF:🛌🏿
1|1F9D1-200D-1F91D-200D-1F9D1|🧑‍🤝‍🧑|people holding hands|握手的人|bae bestie bff couple dating flirt friends hand hold people twins people holding hands people_holding_hands 握手的人 人 情侶 握手 牽手 牽手的人 牽牽|1F9D1-1F3FB-200D-1F91D-200D-1F9D1-1F3FB:🧑🏻‍🤝‍🧑🏻 1F9D1-1F3FB-200D-1F91D-200D-1F9D1-1F3FC:🧑🏻‍🤝‍🧑🏼 1F9D1-1F3FB-200D-1F91D-200D-1F9D1-1F3FD:🧑🏻‍🤝‍🧑🏽 1F9D1-1F3FB-200D-1F91D-200D-1F9D1-1F3FE:🧑🏻‍🤝‍🧑🏾 1F9D1-1F3FB-200D-1F91D-200D-1F9D1-1F3FF:🧑🏻‍🤝‍🧑🏿 1F9D1-1F3FC-200D-1F91D-200D-1F9D1-1F3FB:🧑🏼‍🤝‍🧑🏻 1F9D1-1F3FC-200D-1F91D-200D-1F9D1-1F3FC:🧑🏼‍🤝‍🧑🏼 1F9D1-1F3FC-200D-1F91D-200D-1F9D1-1F3FD:🧑🏼‍🤝‍🧑🏽 1F9D1-1F3FC-200D-1F91D-200D-1F9D1-1F3FE:🧑🏼‍🤝‍🧑🏾 1F9D1-1F3FC-200D-1F91D-200D-1F9D1-1F3FF:🧑🏼‍🤝‍🧑🏿 1F9D1-1F3FD-200D-1F91D-200D-1F9D1-1F3FB:🧑🏽‍🤝‍🧑🏻 1F9D1-1F3FD-200D-1F91D-200D-1F9D1-1F3FC:🧑🏽‍🤝‍🧑🏼 1F9D1-1F3FD-200D-1F91D-200D-1F9D1-1F3FD:🧑🏽‍🤝‍🧑🏽 1F9D1-1F3FD-200D-1F91D-200D-1F9D1-1F3FE:🧑🏽‍🤝‍🧑🏾 1F9D1-1F3FD-200D-1F91D-200D-1F9D1-1F3FF:🧑🏽‍🤝‍🧑🏿 1F9D1-1F3FE-200D-1F91D-200D-1F9D1-1F3FB:🧑🏾‍🤝‍🧑🏻 1F9D1-1F3FE-200D-1F91D-200D-1F9D1-1F3FC:🧑🏾‍🤝‍🧑🏼 1F9D1-1F3FE-200D-1F91D-200D-1F9D1-1F3FD:🧑🏾‍🤝‍🧑🏽 1F9D1-1F3FE-200D-1F91D-200D-1F9D1-1F3FE:🧑🏾‍🤝‍🧑🏾 1F9D1-1F3FE-200D-1F91D-200D-1F9D1-1F3FF:🧑🏾‍🤝‍🧑🏿 1F9D1-1F3FF-200D-1F91D-200D-1F9D1-1F3FB:🧑🏿‍🤝‍🧑🏻 1F9D1-1F3FF-200D-1F91D-200D-1F9D1-1F3FC:🧑🏿‍🤝‍🧑🏼 1F9D1-1F3FF-200D-1F91D-200D-1F9D1-1F3FD:🧑🏿‍🤝‍🧑🏽 1F9D1-1F3FF-200D-1F91D-200D-1F9D1-1F3FE:🧑🏿‍🤝‍🧑🏾 1F9D1-1F3FF-200D-1F91D-200D-1F9D1-1F3FF:🧑🏿‍🤝‍🧑🏿
"""

private const val EMOJI_ROWS_4 = """
1|1F46D|👭|women holding hands|兩個女人|bae bestie bff couple dating flirt friends girls hand hold sisters twins women women holding hands two_women_holding_hands 兩個女人 lgbt 兩個女人手拉手 女兒 女朋友 好朋友 姐妹 姐妹淘 手拉手|1F46D-1F3FB:👭🏻 1F46D-1F3FC:👭🏼 1F46D-1F3FD:👭🏽 1F46D-1F3FE:👭🏾 1F46D-1F3FF:👭🏿 1F469-1F3FB-200D-1F91D-200D-1F469-1F3FC:👩🏻‍🤝‍👩🏼 1F469-1F3FB-200D-1F91D-200D-1F469-1F3FD:👩🏻‍🤝‍👩🏽 1F469-1F3FB-200D-1F91D-200D-1F469-1F3FE:👩🏻‍🤝‍👩🏾 1F469-1F3FB-200D-1F91D-200D-1F469-1F3FF:👩🏻‍🤝‍👩🏿 1F469-1F3FC-200D-1F91D-200D-1F469-1F3FB:👩🏼‍🤝‍👩🏻 1F469-1F3FC-200D-1F91D-200D-1F469-1F3FD:👩🏼‍🤝‍👩🏽 1F469-1F3FC-200D-1F91D-200D-1F469-1F3FE:👩🏼‍🤝‍👩🏾 1F469-1F3FC-200D-1F91D-200D-1F469-1F3FF:👩🏼‍🤝‍👩🏿 1F469-1F3FD-200D-1F91D-200D-1F469-1F3FB:👩🏽‍🤝‍👩🏻 1F469-1F3FD-200D-1F91D-200D-1F469-1F3FC:👩🏽‍🤝‍👩🏼 1F469-1F3FD-200D-1F91D-200D-1F469-1F3FE:👩🏽‍🤝‍👩🏾 1F469-1F3FD-200D-1F91D-200D-1F469-1F3FF:👩🏽‍🤝‍👩🏿 1F469-1F3FE-200D-1F91D-200D-1F469-1F3FB:👩🏾‍🤝‍👩🏻 1F469-1F3FE-200D-1F91D-200D-1F469-1F3FC:👩🏾‍🤝‍👩🏼 1F469-1F3FE-200D-1F91D-200D-1F469-1F3FD:👩🏾‍🤝‍👩🏽 1F469-1F3FE-200D-1F91D-200D-1F469-1F3FF:👩🏾‍🤝‍👩🏿 1F469-1F3FF-200D-1F91D-200D-1F469-1F3FB:👩🏿‍🤝‍👩🏻 1F469-1F3FF-200D-1F91D-200D-1F469-1F3FC:👩🏿‍🤝‍👩🏼 1F469-1F3FF-200D-1F91D-200D-1F469-1F3FD:👩🏿‍🤝‍👩🏽 1F469-1F3FF-200D-1F91D-200D-1F469-1F3FE:👩🏿‍🤝‍👩🏾
1|1F46B|👫|woman and man holding hands|一男一女|bae bestie bff couple dating flirt friends hand hold man twins woman woman and man holding hands 一男一女 一男一女手拉手 手拉手 相戀 約會 調情|1F46B-1F3FB:👫🏻 1F46B-1F3FC:👫🏼 1F46B-1F3FD:👫🏽 1F46B-1F3FE:👫🏾 1F46B-1F3FF:👫🏿 1F469-1F3FB-200D-1F91D-200D-1F468-1F3FC:👩🏻‍🤝‍👨🏼 1F469-1F3FB-200D-1F91D-200D-1F468-1F3FD:👩🏻‍🤝‍👨🏽 1F469-1F3FB-200D-1F91D-200D-1F468-1F3FE:👩🏻‍🤝‍👨🏾 1F469-1F3FB-200D-1F91D-200D-1F468-1F3FF:👩🏻‍🤝‍👨🏿 1F469-1F3FC-200D-1F91D-200D-1F468-1F3FB:👩🏼‍🤝‍👨🏻 1F469-1F3FC-200D-1F91D-200D-1F468-1F3FD:👩🏼‍🤝‍👨🏽 1F469-1F3FC-200D-1F91D-200D-1F468-1F3FE:👩🏼‍🤝‍👨🏾 1F469-1F3FC-200D-1F91D-200D-1F468-1F3FF:👩🏼‍🤝‍👨🏿 1F469-1F3FD-200D-1F91D-200D-1F468-1F3FB:👩🏽‍🤝‍👨🏻 1F469-1F3FD-200D-1F91D-200D-1F468-1F3FC:👩🏽‍🤝‍👨🏼 1F469-1F3FD-200D-1F91D-200D-1F468-1F3FE:👩🏽‍🤝‍👨🏾 1F469-1F3FD-200D-1F91D-200D-1F468-1F3FF:👩🏽‍🤝‍👨🏿 1F469-1F3FE-200D-1F91D-200D-1F468-1F3FB:👩🏾‍🤝‍👨🏻 1F469-1F3FE-200D-1F91D-200D-1F468-1F3FC:👩🏾‍🤝‍👨🏼 1F469-1F3FE-200D-1F91D-200D-1F468-1F3FD:👩🏾‍🤝‍👨🏽 1F469-1F3FE-200D-1F91D-200D-1F468-1F3FF:👩🏾‍🤝‍👨🏿 1F469-1F3FF-200D-1F91D-200D-1F468-1F3FB:👩🏿‍🤝‍👨🏻 1F469-1F3FF-200D-1F91D-200D-1F468-1F3FC:👩🏿‍🤝‍👨🏼 1F469-1F3FF-200D-1F91D-200D-1F468-1F3FD:👩🏿‍🤝‍👨🏽 1F469-1F3FF-200D-1F91D-200D-1F468-1F3FE:👩🏿‍🤝‍👨🏾
1|1F46C|👬|men holding hands|兩個男人|bae bestie bff boys brothers couple dating flirt friends hand hold men twins men holding hands two_men_holding_hands 兩個男人 lgbt 兩個男人手拉手 手拉手 雙胞胎|1F46C-1F3FB:👬🏻 1F46C-1F3FC:👬🏼 1F46C-1F3FD:👬🏽 1F46C-1F3FE:👬🏾 1F46C-1F3FF:👬🏿 1F468-1F3FB-200D-1F91D-200D-1F468-1F3FC:👨🏻‍🤝‍👨🏼 1F468-1F3FB-200D-1F91D-200D-1F468-1F3FD:👨🏻‍🤝‍👨🏽 1F468-1F3FB-200D-1F91D-200D-1F468-1F3FE:👨🏻‍🤝‍👨🏾 1F468-1F3FB-200D-1F91D-200D-1F468-1F3FF:👨🏻‍🤝‍👨🏿 1F468-1F3FC-200D-1F91D-200D-1F468-1F3FB:👨🏼‍🤝‍👨🏻 1F468-1F3FC-200D-1F91D-200D-1F468-1F3FD:👨🏼‍🤝‍👨🏽 1F468-1F3FC-200D-1F91D-200D-1F468-1F3FE:👨🏼‍🤝‍👨🏾 1F468-1F3FC-200D-1F91D-200D-1F468-1F3FF:👨🏼‍🤝‍👨🏿 1F468-1F3FD-200D-1F91D-200D-1F468-1F3FB:👨🏽‍🤝‍👨🏻 1F468-1F3FD-200D-1F91D-200D-1F468-1F3FC:👨🏽‍🤝‍👨🏼 1F468-1F3FD-200D-1F91D-200D-1F468-1F3FE:👨🏽‍🤝‍👨🏾 1F468-1F3FD-200D-1F91D-200D-1F468-1F3FF:👨🏽‍🤝‍👨🏿 1F468-1F3FE-200D-1F91D-200D-1F468-1F3FB:👨🏾‍🤝‍👨🏻 1F468-1F3FE-200D-1F91D-200D-1F468-1F3FC:👨🏾‍🤝‍👨🏼 1F468-1F3FE-200D-1F91D-200D-1F468-1F3FD:👨🏾‍🤝‍👨🏽 1F468-1F3FE-200D-1F91D-200D-1F468-1F3FF:👨🏾‍🤝‍👨🏿 1F468-1F3FF-200D-1F91D-200D-1F468-1F3FB:👨🏿‍🤝‍👨🏻 1F468-1F3FF-200D-1F91D-200D-1F468-1F3FC:👨🏿‍🤝‍👨🏼 1F468-1F3FF-200D-1F91D-200D-1F468-1F3FD:👨🏿‍🤝‍👨🏽 1F468-1F3FF-200D-1F91D-200D-1F468-1F3FE:👨🏿‍🤝‍👨🏾
1|1F48F|💏|kiss|親|anniversary babe bae couple date dating heart love mwah person romance together xoxo kiss couplekiss 親 女朋友 寶貝 愛情 接吻 男朋友 約會|1F48F-1F3FB:💏🏻 1F48F-1F3FC:💏🏼 1F48F-1F3FD:💏🏽 1F48F-1F3FE:💏🏾 1F48F-1F3FF:💏🏿 1F9D1-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FC:🧑🏻‍❤️‍💋‍🧑🏼 1F9D1-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FD:🧑🏻‍❤️‍💋‍🧑🏽 1F9D1-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FE:🧑🏻‍❤️‍💋‍🧑🏾 1F9D1-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FF:🧑🏻‍❤️‍💋‍🧑🏿 1F9D1-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FB:🧑🏼‍❤️‍💋‍🧑🏻 1F9D1-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FD:🧑🏼‍❤️‍💋‍🧑🏽 1F9D1-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FE:🧑🏼‍❤️‍💋‍🧑🏾 1F9D1-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FF:🧑🏼‍❤️‍💋‍🧑🏿 1F9D1-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FB:🧑🏽‍❤️‍💋‍🧑🏻 1F9D1-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FC:🧑🏽‍❤️‍💋‍🧑🏼 1F9D1-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FE:🧑🏽‍❤️‍💋‍🧑🏾 1F9D1-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FF:🧑🏽‍❤️‍💋‍🧑🏿 1F9D1-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FB:🧑🏾‍❤️‍💋‍🧑🏻 1F9D1-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FC:🧑🏾‍❤️‍💋‍🧑🏼 1F9D1-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FD:🧑🏾‍❤️‍💋‍🧑🏽 1F9D1-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FF:🧑🏾‍❤️‍💋‍🧑🏿 1F9D1-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FB:🧑🏿‍❤️‍💋‍🧑🏻 1F9D1-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FC:🧑🏿‍❤️‍💋‍🧑🏼 1F9D1-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FD:🧑🏿‍❤️‍💋‍🧑🏽 1F9D1-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F9D1-1F3FE:🧑🏿‍❤️‍💋‍🧑🏾
1|1F469-200D-2764-FE0F-200D-1F48B-200D-1F468|👩‍❤️‍💋‍👨|kiss: woman, man|親：女人 男人|anniversary babe bae couple date dating heart kiss love man mwah person romance together woman xoxo kiss: woman, man couplekiss_man_woman 親：女人 男人 女人 女朋友 寶貝 愛情 接吻 男人 男朋友 約會 親|1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👩🏻‍❤️‍💋‍👨🏻 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👩🏻‍❤️‍💋‍👨🏼 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👩🏻‍❤️‍💋‍👨🏽 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👩🏻‍❤️‍💋‍👨🏾 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👩🏻‍❤️‍💋‍👨🏿 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👩🏼‍❤️‍💋‍👨🏻 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👩🏼‍❤️‍💋‍👨🏼 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👩🏼‍❤️‍💋‍👨🏽 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👩🏼‍❤️‍💋‍👨🏾 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👩🏼‍❤️‍💋‍👨🏿 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👩🏽‍❤️‍💋‍👨🏻 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👩🏽‍❤️‍💋‍👨🏼 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👩🏽‍❤️‍💋‍👨🏽 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👩🏽‍❤️‍💋‍👨🏾 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👩🏽‍❤️‍💋‍👨🏿 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👩🏾‍❤️‍💋‍👨🏻 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👩🏾‍❤️‍💋‍👨🏼 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👩🏾‍❤️‍💋‍👨🏽 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👩🏾‍❤️‍💋‍👨🏾 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👩🏾‍❤️‍💋‍👨🏿 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👩🏿‍❤️‍💋‍👨🏻 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👩🏿‍❤️‍💋‍👨🏼 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👩🏿‍❤️‍💋‍👨🏽 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👩🏿‍❤️‍💋‍👨🏾 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👩🏿‍❤️‍💋‍👨🏿
1|1F468-200D-2764-FE0F-200D-1F48B-200D-1F468|👨‍❤️‍💋‍👨|kiss: man, man|親：男人 男人|anniversary babe bae couple date dating heart kiss love man mwah person romance together xoxo kiss: man, man couplekiss_man_man 親：男人 男人 女朋友 寶貝 愛情 接吻 男人 男朋友 約會 親|1F468-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👨🏻‍❤️‍💋‍👨🏻 1F468-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👨🏻‍❤️‍💋‍👨🏼 1F468-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👨🏻‍❤️‍💋‍👨🏽 1F468-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👨🏻‍❤️‍💋‍👨🏾 1F468-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👨🏻‍❤️‍💋‍👨🏿 1F468-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👨🏼‍❤️‍💋‍👨🏻 1F468-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👨🏼‍❤️‍💋‍👨🏼 1F468-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👨🏼‍❤️‍💋‍👨🏽 1F468-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👨🏼‍❤️‍💋‍👨🏾 1F468-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👨🏼‍❤️‍💋‍👨🏿 1F468-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👨🏽‍❤️‍💋‍👨🏻 1F468-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👨🏽‍❤️‍💋‍👨🏼 1F468-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👨🏽‍❤️‍💋‍👨🏽 1F468-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👨🏽‍❤️‍💋‍👨🏾 1F468-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👨🏽‍❤️‍💋‍👨🏿 1F468-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👨🏾‍❤️‍💋‍👨🏻 1F468-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👨🏾‍❤️‍💋‍👨🏼 1F468-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👨🏾‍❤️‍💋‍👨🏽 1F468-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👨🏾‍❤️‍💋‍👨🏾 1F468-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👨🏾‍❤️‍💋‍👨🏿 1F468-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FB:👨🏿‍❤️‍💋‍👨🏻 1F468-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FC:👨🏿‍❤️‍💋‍👨🏼 1F468-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FD:👨🏿‍❤️‍💋‍👨🏽 1F468-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FE:👨🏿‍❤️‍💋‍👨🏾 1F468-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F468-1F3FF:👨🏿‍❤️‍💋‍👨🏿
1|1F469-200D-2764-FE0F-200D-1F48B-200D-1F469|👩‍❤️‍💋‍👩|kiss: woman, woman|親：女人 女人|anniversary babe bae couple date dating heart kiss love mwah person romance together woman xoxo kiss: woman, woman couplekiss_woman_woman 親：女人 女人 女人 女朋友 寶貝 愛情 接吻 男朋友 約會 親|1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FB:👩🏻‍❤️‍💋‍👩🏻 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FC:👩🏻‍❤️‍💋‍👩🏼 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FD:👩🏻‍❤️‍💋‍👩🏽 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FE:👩🏻‍❤️‍💋‍👩🏾 1F469-1F3FB-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FF:👩🏻‍❤️‍💋‍👩🏿 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FB:👩🏼‍❤️‍💋‍👩🏻 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FC:👩🏼‍❤️‍💋‍👩🏼 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FD:👩🏼‍❤️‍💋‍👩🏽 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FE:👩🏼‍❤️‍💋‍👩🏾 1F469-1F3FC-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FF:👩🏼‍❤️‍💋‍👩🏿 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FB:👩🏽‍❤️‍💋‍👩🏻 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FC:👩🏽‍❤️‍💋‍👩🏼 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FD:👩🏽‍❤️‍💋‍👩🏽 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FE:👩🏽‍❤️‍💋‍👩🏾 1F469-1F3FD-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FF:👩🏽‍❤️‍💋‍👩🏿 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FB:👩🏾‍❤️‍💋‍👩🏻 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FC:👩🏾‍❤️‍💋‍👩🏼 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FD:👩🏾‍❤️‍💋‍👩🏽 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FE:👩🏾‍❤️‍💋‍👩🏾 1F469-1F3FE-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FF:👩🏾‍❤️‍💋‍👩🏿 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FB:👩🏿‍❤️‍💋‍👩🏻 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FC:👩🏿‍❤️‍💋‍👩🏼 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FD:👩🏿‍❤️‍💋‍👩🏽 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FE:👩🏿‍❤️‍💋‍👩🏾 1F469-1F3FF-200D-2764-FE0F-200D-1F48B-200D-1F469-1F3FF:👩🏿‍❤️‍💋‍👩🏿
1|1F491|💑|couple with heart|相愛|anniversary babe bae couple dating heart kiss love person relationship romance together you couple with heart couple_with_heart 相愛 一對 男女|1F491-1F3FB:💑🏻 1F491-1F3FC:💑🏼 1F491-1F3FD:💑🏽 1F491-1F3FE:💑🏾 1F491-1F3FF:💑🏿 1F9D1-1F3FB-200D-2764-FE0F-200D-1F9D1-1F3FC:🧑🏻‍❤️‍🧑🏼 1F9D1-1F3FB-200D-2764-FE0F-200D-1F9D1-1F3FD:🧑🏻‍❤️‍🧑🏽 1F9D1-1F3FB-200D-2764-FE0F-200D-1F9D1-1F3FE:🧑🏻‍❤️‍🧑🏾 1F9D1-1F3FB-200D-2764-FE0F-200D-1F9D1-1F3FF:🧑🏻‍❤️‍🧑🏿 1F9D1-1F3FC-200D-2764-FE0F-200D-1F9D1-1F3FB:🧑🏼‍❤️‍🧑🏻 1F9D1-1F3FC-200D-2764-FE0F-200D-1F9D1-1F3FD:🧑🏼‍❤️‍🧑🏽 1F9D1-1F3FC-200D-2764-FE0F-200D-1F9D1-1F3FE:🧑🏼‍❤️‍🧑🏾 1F9D1-1F3FC-200D-2764-FE0F-200D-1F9D1-1F3FF:🧑🏼‍❤️‍🧑🏿 1F9D1-1F3FD-200D-2764-FE0F-200D-1F9D1-1F3FB:🧑🏽‍❤️‍🧑🏻 1F9D1-1F3FD-200D-2764-FE0F-200D-1F9D1-1F3FC:🧑🏽‍❤️‍🧑🏼 1F9D1-1F3FD-200D-2764-FE0F-200D-1F9D1-1F3FE:🧑🏽‍❤️‍🧑🏾 1F9D1-1F3FD-200D-2764-FE0F-200D-1F9D1-1F3FF:🧑🏽‍❤️‍🧑🏿 1F9D1-1F3FE-200D-2764-FE0F-200D-1F9D1-1F3FB:🧑🏾‍❤️‍🧑🏻 1F9D1-1F3FE-200D-2764-FE0F-200D-1F9D1-1F3FC:🧑🏾‍❤️‍🧑🏼 1F9D1-1F3FE-200D-2764-FE0F-200D-1F9D1-1F3FD:🧑🏾‍❤️‍🧑🏽 1F9D1-1F3FE-200D-2764-FE0F-200D-1F9D1-1F3FF:🧑🏾‍❤️‍🧑🏿 1F9D1-1F3FF-200D-2764-FE0F-200D-1F9D1-1F3FB:🧑🏿‍❤️‍🧑🏻 1F9D1-1F3FF-200D-2764-FE0F-200D-1F9D1-1F3FC:🧑🏿‍❤️‍🧑🏼 1F9D1-1F3FF-200D-2764-FE0F-200D-1F9D1-1F3FD:🧑🏿‍❤️‍🧑🏽 1F9D1-1F3FF-200D-2764-FE0F-200D-1F9D1-1F3FE:🧑🏿‍❤️‍🧑🏾
1|1F469-200D-2764-FE0F-200D-1F468|👩‍❤️‍👨|couple with heart: woman, man|相愛：女人 男人|anniversary babe bae couple dating heart kiss love man person relationship romance together woman you couple with heart: woman, man couple_with_heart_woman_man 相愛：女人 男人 一對 女人 男人 男女 相愛|1F469-1F3FB-200D-2764-FE0F-200D-1F468-1F3FB:👩🏻‍❤️‍👨🏻 1F469-1F3FB-200D-2764-FE0F-200D-1F468-1F3FC:👩🏻‍❤️‍👨🏼 1F469-1F3FB-200D-2764-FE0F-200D-1F468-1F3FD:👩🏻‍❤️‍👨🏽 1F469-1F3FB-200D-2764-FE0F-200D-1F468-1F3FE:👩🏻‍❤️‍👨🏾 1F469-1F3FB-200D-2764-FE0F-200D-1F468-1F3FF:👩🏻‍❤️‍👨🏿 1F469-1F3FC-200D-2764-FE0F-200D-1F468-1F3FB:👩🏼‍❤️‍👨🏻 1F469-1F3FC-200D-2764-FE0F-200D-1F468-1F3FC:👩🏼‍❤️‍👨🏼 1F469-1F3FC-200D-2764-FE0F-200D-1F468-1F3FD:👩🏼‍❤️‍👨🏽 1F469-1F3FC-200D-2764-FE0F-200D-1F468-1F3FE:👩🏼‍❤️‍👨🏾 1F469-1F3FC-200D-2764-FE0F-200D-1F468-1F3FF:👩🏼‍❤️‍👨🏿 1F469-1F3FD-200D-2764-FE0F-200D-1F468-1F3FB:👩🏽‍❤️‍👨🏻 1F469-1F3FD-200D-2764-FE0F-200D-1F468-1F3FC:👩🏽‍❤️‍👨🏼 1F469-1F3FD-200D-2764-FE0F-200D-1F468-1F3FD:👩🏽‍❤️‍👨🏽 1F469-1F3FD-200D-2764-FE0F-200D-1F468-1F3FE:👩🏽‍❤️‍👨🏾 1F469-1F3FD-200D-2764-FE0F-200D-1F468-1F3FF:👩🏽‍❤️‍👨🏿 1F469-1F3FE-200D-2764-FE0F-200D-1F468-1F3FB:👩🏾‍❤️‍👨🏻 1F469-1F3FE-200D-2764-FE0F-200D-1F468-1F3FC:👩🏾‍❤️‍👨🏼 1F469-1F3FE-200D-2764-FE0F-200D-1F468-1F3FD:👩🏾‍❤️‍👨🏽 1F469-1F3FE-200D-2764-FE0F-200D-1F468-1F3FE:👩🏾‍❤️‍👨🏾 1F469-1F3FE-200D-2764-FE0F-200D-1F468-1F3FF:👩🏾‍❤️‍👨🏿 1F469-1F3FF-200D-2764-FE0F-200D-1F468-1F3FB:👩🏿‍❤️‍👨🏻 1F469-1F3FF-200D-2764-FE0F-200D-1F468-1F3FC:👩🏿‍❤️‍👨🏼 1F469-1F3FF-200D-2764-FE0F-200D-1F468-1F3FD:👩🏿‍❤️‍👨🏽 1F469-1F3FF-200D-2764-FE0F-200D-1F468-1F3FE:👩🏿‍❤️‍👨🏾 1F469-1F3FF-200D-2764-FE0F-200D-1F468-1F3FF:👩🏿‍❤️‍👨🏿
1|1F468-200D-2764-FE0F-200D-1F468|👨‍❤️‍👨|couple with heart: man, man|相愛：男人 男人|anniversary babe bae couple dating heart kiss love man person relationship romance together you couple with heart: man, man couple_with_heart_man_man 相愛：男人 男人 一對 男人 男女 相愛|1F468-1F3FB-200D-2764-FE0F-200D-1F468-1F3FB:👨🏻‍❤️‍👨🏻 1F468-1F3FB-200D-2764-FE0F-200D-1F468-1F3FC:👨🏻‍❤️‍👨🏼 1F468-1F3FB-200D-2764-FE0F-200D-1F468-1F3FD:👨🏻‍❤️‍👨🏽 1F468-1F3FB-200D-2764-FE0F-200D-1F468-1F3FE:👨🏻‍❤️‍👨🏾 1F468-1F3FB-200D-2764-FE0F-200D-1F468-1F3FF:👨🏻‍❤️‍👨🏿 1F468-1F3FC-200D-2764-FE0F-200D-1F468-1F3FB:👨🏼‍❤️‍👨🏻 1F468-1F3FC-200D-2764-FE0F-200D-1F468-1F3FC:👨🏼‍❤️‍👨🏼 1F468-1F3FC-200D-2764-FE0F-200D-1F468-1F3FD:👨🏼‍❤️‍👨🏽 1F468-1F3FC-200D-2764-FE0F-200D-1F468-1F3FE:👨🏼‍❤️‍👨🏾 1F468-1F3FC-200D-2764-FE0F-200D-1F468-1F3FF:👨🏼‍❤️‍👨🏿 1F468-1F3FD-200D-2764-FE0F-200D-1F468-1F3FB:👨🏽‍❤️‍👨🏻 1F468-1F3FD-200D-2764-FE0F-200D-1F468-1F3FC:👨🏽‍❤️‍👨🏼 1F468-1F3FD-200D-2764-FE0F-200D-1F468-1F3FD:👨🏽‍❤️‍👨🏽 1F468-1F3FD-200D-2764-FE0F-200D-1F468-1F3FE:👨🏽‍❤️‍👨🏾 1F468-1F3FD-200D-2764-FE0F-200D-1F468-1F3FF:👨🏽‍❤️‍👨🏿 1F468-1F3FE-200D-2764-FE0F-200D-1F468-1F3FB:👨🏾‍❤️‍👨🏻 1F468-1F3FE-200D-2764-FE0F-200D-1F468-1F3FC:👨🏾‍❤️‍👨🏼 1F468-1F3FE-200D-2764-FE0F-200D-1F468-1F3FD:👨🏾‍❤️‍👨🏽 1F468-1F3FE-200D-2764-FE0F-200D-1F468-1F3FE:👨🏾‍❤️‍👨🏾 1F468-1F3FE-200D-2764-FE0F-200D-1F468-1F3FF:👨🏾‍❤️‍👨🏿 1F468-1F3FF-200D-2764-FE0F-200D-1F468-1F3FB:👨🏿‍❤️‍👨🏻 1F468-1F3FF-200D-2764-FE0F-200D-1F468-1F3FC:👨🏿‍❤️‍👨🏼 1F468-1F3FF-200D-2764-FE0F-200D-1F468-1F3FD:👨🏿‍❤️‍👨🏽 1F468-1F3FF-200D-2764-FE0F-200D-1F468-1F3FE:👨🏿‍❤️‍👨🏾 1F468-1F3FF-200D-2764-FE0F-200D-1F468-1F3FF:👨🏿‍❤️‍👨🏿
1|1F469-200D-2764-FE0F-200D-1F469|👩‍❤️‍👩|couple with heart: woman, woman|相愛：女人 女人|anniversary babe bae couple dating heart kiss love person relationship romance together woman you couple with heart: woman, woman couple_with_heart_woman_woman 相愛：女人 女人 一對 女人 男女 相愛|1F469-1F3FB-200D-2764-FE0F-200D-1F469-1F3FB:👩🏻‍❤️‍👩🏻 1F469-1F3FB-200D-2764-FE0F-200D-1F469-1F3FC:👩🏻‍❤️‍👩🏼 1F469-1F3FB-200D-2764-FE0F-200D-1F469-1F3FD:👩🏻‍❤️‍👩🏽 1F469-1F3FB-200D-2764-FE0F-200D-1F469-1F3FE:👩🏻‍❤️‍👩🏾 1F469-1F3FB-200D-2764-FE0F-200D-1F469-1F3FF:👩🏻‍❤️‍👩🏿 1F469-1F3FC-200D-2764-FE0F-200D-1F469-1F3FB:👩🏼‍❤️‍👩🏻 1F469-1F3FC-200D-2764-FE0F-200D-1F469-1F3FC:👩🏼‍❤️‍👩🏼 1F469-1F3FC-200D-2764-FE0F-200D-1F469-1F3FD:👩🏼‍❤️‍👩🏽 1F469-1F3FC-200D-2764-FE0F-200D-1F469-1F3FE:👩🏼‍❤️‍👩🏾 1F469-1F3FC-200D-2764-FE0F-200D-1F469-1F3FF:👩🏼‍❤️‍👩🏿 1F469-1F3FD-200D-2764-FE0F-200D-1F469-1F3FB:👩🏽‍❤️‍👩🏻 1F469-1F3FD-200D-2764-FE0F-200D-1F469-1F3FC:👩🏽‍❤️‍👩🏼 1F469-1F3FD-200D-2764-FE0F-200D-1F469-1F3FD:👩🏽‍❤️‍👩🏽 1F469-1F3FD-200D-2764-FE0F-200D-1F469-1F3FE:👩🏽‍❤️‍👩🏾 1F469-1F3FD-200D-2764-FE0F-200D-1F469-1F3FF:👩🏽‍❤️‍👩🏿 1F469-1F3FE-200D-2764-FE0F-200D-1F469-1F3FB:👩🏾‍❤️‍👩🏻 1F469-1F3FE-200D-2764-FE0F-200D-1F469-1F3FC:👩🏾‍❤️‍👩🏼 1F469-1F3FE-200D-2764-FE0F-200D-1F469-1F3FD:👩🏾‍❤️‍👩🏽 1F469-1F3FE-200D-2764-FE0F-200D-1F469-1F3FE:👩🏾‍❤️‍👩🏾 1F469-1F3FE-200D-2764-FE0F-200D-1F469-1F3FF:👩🏾‍❤️‍👩🏿 1F469-1F3FF-200D-2764-FE0F-200D-1F469-1F3FB:👩🏿‍❤️‍👩🏻 1F469-1F3FF-200D-2764-FE0F-200D-1F469-1F3FC:👩🏿‍❤️‍👩🏼 1F469-1F3FF-200D-2764-FE0F-200D-1F469-1F3FD:👩🏿‍❤️‍👩🏽 1F469-1F3FF-200D-2764-FE0F-200D-1F469-1F3FE:👩🏿‍❤️‍👩🏾 1F469-1F3FF-200D-2764-FE0F-200D-1F469-1F3FF:👩🏿‍❤️‍👩🏿
1|1F468-200D-1F469-200D-1F466|👨‍👩‍👦|family: man, woman, boy|家庭：男人 女人 男孩|boy child family man woman family: man, woman, boy family_man_woman_boy 家庭：男人 女人 男孩 女人 家庭 男人 男孩 親子|
1|1F468-200D-1F469-200D-1F467|👨‍👩‍👧|family: man, woman, girl|家庭：男人 女人 女孩|child family girl man woman family: man, woman, girl family_man_woman_girl 家庭：男人 女人 女孩 女人 女孩 家庭 男人 親子|
1|1F468-200D-1F469-200D-1F467-200D-1F466|👨‍👩‍👧‍👦|family: man, woman, girl, boy|家庭：男人 女人 女孩 男孩|boy child family girl man woman family: man, woman, girl, boy family_man_woman_girl_boy 家庭：男人 女人 女孩 男孩 女人 女孩 家庭 男人 男孩 親子|
1|1F468-200D-1F469-200D-1F466-200D-1F466|👨‍👩‍👦‍👦|family: man, woman, boy, boy|家庭：男人 女人 男孩 男孩|boy child family man woman family: man, woman, boy, boy family_man_woman_boy_boy 家庭：男人 女人 男孩 男孩 女人 家庭 男人 男孩 親子|
1|1F468-200D-1F469-200D-1F467-200D-1F467|👨‍👩‍👧‍👧|family: man, woman, girl, girl|家庭：男人 女人 女孩 女孩|child family girl man woman family: man, woman, girl, girl family_man_woman_girl_girl 家庭：男人 女人 女孩 女孩 女人 女孩 家庭 男人 親子|
1|1F468-200D-1F468-200D-1F466|👨‍👨‍👦|family: man, man, boy|家庭：男人 男人 男孩|boy child family man family: man, man, boy family_man_man_boy 家庭：男人 男人 男孩 家庭 男人 男孩 親子|
1|1F468-200D-1F468-200D-1F467|👨‍👨‍👧|family: man, man, girl|家庭：男人 男人 女孩|child family girl man family: man, man, girl family_man_man_girl 家庭：男人 男人 女孩 女孩 家庭 男人 親子|
1|1F468-200D-1F468-200D-1F467-200D-1F466|👨‍👨‍👧‍👦|family: man, man, girl, boy|家庭：男人 男人 女孩 男孩|boy child family girl man family: man, man, girl, boy family_man_man_girl_boy 家庭：男人 男人 女孩 男孩 女孩 家庭 男人 男孩 親子|
1|1F468-200D-1F468-200D-1F466-200D-1F466|👨‍👨‍👦‍👦|family: man, man, boy, boy|家庭：男人 男人 男孩 男孩|boy child family man family: man, man, boy, boy family_man_man_boy_boy 家庭：男人 男人 男孩 男孩 家庭 男人 男孩 親子|
1|1F468-200D-1F468-200D-1F467-200D-1F467|👨‍👨‍👧‍👧|family: man, man, girl, girl|家庭：男人 男人 女孩 女孩|child family girl man family: man, man, girl, girl family_man_man_girl_girl 家庭：男人 男人 女孩 女孩 女孩 家庭 男人 親子|
1|1F469-200D-1F469-200D-1F466|👩‍👩‍👦|family: woman, woman, boy|家庭：女人 女人 男孩|boy child family woman family: woman, woman, boy family_woman_woman_boy 家庭：女人 女人 男孩 女人 家庭 男孩 親子|
1|1F469-200D-1F469-200D-1F467|👩‍👩‍👧|family: woman, woman, girl|家庭：女人 女人 女孩|child family girl woman family: woman, woman, girl family_woman_woman_girl 家庭：女人 女人 女孩 女人 女孩 家庭 親子|
1|1F469-200D-1F469-200D-1F467-200D-1F466|👩‍👩‍👧‍👦|family: woman, woman, girl, boy|家庭：女人 女人 女孩 男孩|boy child family girl woman family: woman, woman, girl, boy family_woman_woman_girl_boy 家庭：女人 女人 女孩 男孩 女人 女孩 家庭 男孩 親子|
1|1F469-200D-1F469-200D-1F466-200D-1F466|👩‍👩‍👦‍👦|family: woman, woman, boy, boy|家庭：女人 女人 男孩 男孩|boy child family woman family: woman, woman, boy, boy family_woman_woman_boy_boy 家庭：女人 女人 男孩 男孩 女人 家庭 男孩 親子|
1|1F469-200D-1F469-200D-1F467-200D-1F467|👩‍👩‍👧‍👧|family: woman, woman, girl, girl|家庭：女人 女人 女孩 女孩|child family girl woman family: woman, woman, girl, girl family_woman_woman_girl_girl 家庭：女人 女人 女孩 女孩 女人 女孩 家庭 親子|
1|1F468-200D-1F466|👨‍👦|family: man, boy|家庭：男人 男孩|boy child family man family: man, boy family_man_boy 家庭：男人 男孩 家庭 男人 男孩 親子|
1|1F468-200D-1F466-200D-1F466|👨‍👦‍👦|family: man, boy, boy|家庭：男人 男孩 男孩|boy child family man family: man, boy, boy family_man_boy_boy 家庭：男人 男孩 男孩 家庭 男人 男孩 親子|
1|1F468-200D-1F467|👨‍👧|family: man, girl|家庭：男人 女孩|child family girl man family: man, girl family_man_girl 家庭：男人 女孩 女孩 家庭 男人 親子|
1|1F468-200D-1F467-200D-1F466|👨‍👧‍👦|family: man, girl, boy|家庭：男人 女孩 男孩|boy child family girl man family: man, girl, boy family_man_girl_boy 家庭：男人 女孩 男孩 女孩 家庭 男人 男孩 親子|
1|1F468-200D-1F467-200D-1F467|👨‍👧‍👧|family: man, girl, girl|家庭：男人 女孩 女孩|child family girl man family: man, girl, girl family_man_girl_girl 家庭：男人 女孩 女孩 女孩 家庭 男人 親子|
1|1F469-200D-1F466|👩‍👦|family: woman, boy|家庭：女人 男孩|boy child family woman family: woman, boy family_woman_boy 家庭：女人 男孩 女人 家庭 男孩 親子|
1|1F469-200D-1F466-200D-1F466|👩‍👦‍👦|family: woman, boy, boy|家庭：女人 男孩 男孩|boy child family woman family: woman, boy, boy family_woman_boy_boy 家庭：女人 男孩 男孩 女人 家庭 男孩 親子|
1|1F469-200D-1F467|👩‍👧|family: woman, girl|家庭：女人 女孩|child family girl woman family: woman, girl family_woman_girl 家庭：女人 女孩 女人 女孩 家庭 親子|
1|1F469-200D-1F467-200D-1F466|👩‍👧‍👦|family: woman, girl, boy|家庭：女人 女孩 男孩|boy child family girl woman family: woman, girl, boy family_woman_girl_boy 家庭：女人 女孩 男孩 女人 女孩 家庭 男孩 親子|
1|1F469-200D-1F467-200D-1F467|👩‍👧‍👧|family: woman, girl, girl|家庭：女人 女孩 女孩|child family girl woman family: woman, girl, girl family_woman_girl_girl 家庭：女人 女孩 女孩 女人 女孩 家庭 親子|
1|1F5E3|🗣️|speaking head|說話的人影|face head silhouette speak speaking speaking head speaking_head 說話的人影 剪影 說話|
1|1F464|👤|bust in silhouette|肖像剪影|bust mysterious shadow silhouette bust in silhouette bust_in_silhouette 肖像剪影 剪影 神秘 陰影|
1|1F465|👥|busts in silhouette|雙人肖像剪影|bff bust busts everyone friend friends people silhouette busts in silhouette busts_in_silhouette 雙人肖像剪影 剪影 朋友 每個人|
1|1FAC2|🫂|people hugging|擁抱的人|comfort embrace farewell friendship goodbye hello hug hugging love people thanks people hugging people_hugging 擁抱的人 再見 友情 安慰 愛 抱抱 掰掰 擁抱 謝謝 道別|
1|1F46A|👪️|family|家庭|child family 家庭 親子|
1|1F9D1-200D-1F9D1-200D-1F9D2|🧑‍🧑‍🧒|family: adult, adult, child|家庭：兩大一小|adult child family family: adult, adult, child 家庭：兩大一小 家庭：兩大一小|
1|1F9D1-200D-1F9D1-200D-1F9D2-200D-1F9D2|🧑‍🧑‍🧒‍🧒|family: adult, adult, child, child|家庭：兩大兩小|adult child family family: adult, adult, child, child 家庭：兩大兩小 家庭：兩大兩小|
1|1F9D1-200D-1F9D2|🧑‍🧒|family: adult, child|家庭：一大一小|adult child family family: adult, child 家庭：一大一小 家庭：一大一小|
1|1F9D1-200D-1F9D2-200D-1F9D2|🧑‍🧒‍🧒|family: adult, child, child|家庭：一大兩小|adult child family family: adult, child, child 家庭：一大兩小 家庭：一大兩小|
1|1F463|👣|footprints|腳印|barefoot clothing footprint omw print walk footprints 腳印 在路上 裸足 赤腳 足跡|
1|1FAC6|🫆|fingerprint|指紋|clue crime detective forensics identity mystery print safety trace fingerprint 指紋 安全 法醫 身份|
3|1F435|🐵|monkey face|猴子頭|animal banana face monkey monkey face monkey_face 猴子頭 動物 猴|
3|1F412|🐒|monkey|猴子|animal banana monkey 猴子 動物 猴 香蕉|
3|1F98D|🦍|gorilla|大猩猩|animal gorilla 大猩猩 動物 猩猩|
3|1F9A7|🦧|orangutan|猩猩|animal ape monkey orangutan 猩猩 人猿 動物 猴子|
3|1F436|🐶|dog face|狗頭|adorbs animal dog face pet puppies puppy dog face 狗頭 寵物 小狗 狗 狗臉|
3|1F415|🐕️|dog|狗|animal animals dogs pet dog dog2 狗 動物|
3|1F9AE|🦮|guide dog|導盲犬|accessibility animal blind dog guide guide dog guide_dog 導盲犬 導盲 行動不便 視障|
3|1F415-200D-1F9BA|🐕‍🦺|service dog|服務犬|accessibility animal assistance dog service service dog service_dog 服務犬 服務 狗 行動不便 輔助|
3|1F429|🐩|poodle|貴賓犬|animal dog fluffy poodle 貴賓犬 捲毛狗 貴賓狗|
3|1F43A|🐺|wolf|狼|animal face wolf 狼 動物 狼面|
3|1F98A|🦊|fox|狐狸|animal face fox fox_face 狐狸 動物|
3|1F99D|🦝|raccoon|浣熊|animal curious sly raccoon 浣熊 好奇 淘氣 狡猾|
3|1F431|🐱|cat face|貓頭|animal cat face kitten kitty pet cat face 貓頭 動物 貓|
3|1F408|🐈️|cat|貓|animal animals cats kitten pet cat cat2 貓 小貓|
3|1F408-200D-2B1B|🐈‍⬛|black cat|黑貓|animal black cat feline halloween meow unlucky black cat black_cat 黑貓 不幸 動物 喵 萬聖節 貓 貓咪 黑 黑色|
3|1F981|🦁|lion|獅子|alpha animal face leo mane order rawr roar safari strong zodiac lion 獅子 獅 獅子座|
3|1F42F|🐯|tiger face|老虎頭|animal big cat face predator tiger tiger face 老虎頭 巧虎 虎 虎面|
3|1F405|🐅|tiger|老虎|animal big cat predator zoo tiger tiger2 老虎 動物 掠食動物 虎|
3|1F406|🐆|leopard|花豹|animal big cat predator zoo leopard 花豹 豹 豹子|
3|1F434|🐴|horse face|馬頭|animal dressage equine face farm horse horses horse face 馬頭 動物 盛裝馬術 馬 馬術|
3|1FACE|🫎|moose|麋鹿|alces animal antlers elk mammal moose 麋鹿 動物 哺乳動物 駝鹿 麋 麋鹿，動物，角，駝鹿，麋，哺乳類|
3|1FACF|🫏|donkey|驢|animal ass burro hinny mammal mule stubborn donkey 驢 傻子 動物 哺乳動物 白癡 笨蛋 頑固 騾 驢子 驢子，動物，騾子，驢子，固執，哺乳類|
3|1F40E|🐎|horse|馬|animal equestrian farm racehorse racing horse 馬 動物 賽馬 馬術|
3|1F984|🦄|unicorn|獨角獸|face unicorn 獨角獸 獨角獸|
3|1F993|🦓|zebra|斑馬|animal stripe zebra 斑馬 動物 條紋|
3|1F98C|🦌|deer|鹿|animal deer 鹿 動物|
3|1F9AC|🦬|bison|野牛|animal buffalo herd wisent bison 野牛 動物 水牛 牧群|
3|1F42E|🐮|cow face|牛頭|animal cow face farm milk moo cow face 牛頭 牛|
3|1F402|🐂|ox|公牛|animal animals bull farm taurus zodiac ox 公牛 動物 牛 金牛座|
3|1F403|🐃|water buffalo|水牛|animal buffalo water zoo water buffalo water_buffalo 水牛 動物 牛|
3|1F404|🐄|cow|乳牛|animal animals farm milk moo cow cow2 乳牛 動物 牛|
3|1F437|🐷|pig face|豬頭|animal bacon face farm pig pork pig face 豬頭 豬|
3|1F416|🐖|pig|豬|animal bacon farm pork sow pig pig2 豬 動物 培根 豬肉|
3|1F417|🐗|boar|野豬|animal pig boar 野豬 動物 豬|
3|1F43D|🐽|pig nose|豬鼻子|animal face farm nose pig smell snout pig nose pig_nose 豬鼻子 豬鼻子|
3|1F40F|🐏|ram|公羊|animal aries horns male sheep zodiac zoo ram 公羊 動物 白羊座 羊|
3|1F411|🐑|ewe|綿羊|animal baa farm female fluffy lamb sheep wool ewe 綿羊 動物 毛茸茸 羊 羊毛|
3|1F410|🐐|goat|山羊|animal capricorn farm milk zodiac goat 山羊 動物 摩羯座 羊|
3|1F42A|🐪|camel|單峰駱駝|animal desert dromedary hump one camel dromedary_camel 單峰駱駝 動物 駱駝|
3|1F42B|🐫|two-hump camel|雙峰駱駝|animal bactrian camel desert hump two two-hump two-hump camel 雙峰駱駝 動物 雙蜂駱駝 駱駝|
3|1F999|🦙|llama|羊駝|alpaca animal guanaco vicuña wool llama 羊駝 動物 小羊駝 毛 羊毛 羊駝毛 美野生羊駝|
3|1F992|🦒|giraffe|長頸鹿|animal spots giraffe 長頸鹿 長頸鹿|
3|1F418|🐘|elephant|大象|animal elephant 大象 象|
3|1F9A3|🦣|mammoth|毛象|animal extinction large tusk wooly mammoth 毛象 動物 巨大 猛瑪象 絕種 象牙 長毛象|
3|1F98F|🦏|rhinoceros|犀牛|animal rhinoceros 犀牛 動物|
3|1F99B|🦛|hippopotamus|河馬|animal hippo hippopotamus 河馬 動物|
3|1F42D|🐭|mouse face|老鼠頭|animal face mouse mouse face 老鼠頭 動物 耗子 鼠|
3|1F401|🐁|mouse|小老鼠|animal animals mouse mouse2 小老鼠 動物 耗子 鼠|
3|1F400|🐀|rat|老鼠|animal rat 老鼠 動物 耗子 鼠|
3|1F439|🐹|hamster|倉鼠|animal face pet hamster 倉鼠 寵物鼠|
3|1F430|🐰|rabbit face|兔子頭|animal bunny face pet rabbit rabbit face 兔子頭 兔 兔子臉 動物|
3|1F407|🐇|rabbit|兔子|animal bunny pet rabbit rabbit2 兔子 兔 動物|
3|1F43F|🐿️|chipmunk|松鼠|animal squirrel chipmunk 松鼠 花栗鼠|
3|1F9AB|🦫|beaver|海狸|animal dam teeth beaver 海狸 動物 水壩 牙|
3|1F994|🦔|hedgehog|刺蝟|animal spiny hedgehog 刺蝟 多刺|
3|1F987|🦇|bat|蝙蝠|animal vampire bat 蝙蝠 吸血|
3|1F43B|🐻|bear|熊|animal face grizzly growl honey bear 熊 動物 熊面|
3|1F43B-200D-2744-FE0F|🐻‍❄️|polar bear|北極熊|animal arctic bear polar white polar bear polar_bear 北極熊 極地 熊 白色|
3|1F428|🐨|koala|無尾熊|animal australia bear down face marsupial under koala 無尾熊 無尾熊|
3|1F43C|🐼|panda|熊貓|animal bamboo face panda panda_face 熊貓 貓熊|
3|1F9A5|🦥|sloth|樹懶|lazy slow sloth 樹懶 慢 懶 懶散|
3|1F9A6|🦦|otter|水獺|animal fishing playful otter 水獺 好玩 釣魚|
3|1F9A8|🦨|skunk|臭鼬|animal stink skunk 臭鼬 臭|
3|1F998|🦘|kangaroo|袋鼠|animal joey jump marsupial kangaroo 袋鼠 動物 小袋鼠 有袋動物 有袋目 澳洲 跳|
3|1F9A1|🦡|badger|獾|animal honey pester badger 獾 動物 糾纏 蜜獾|
3|1F43E|🐾|paw prints|動物腳印|feet paw paws print prints paw prints paw_prints 動物腳印 足跡|
3|1F983|🦃|turkey|火雞|bird gobble thanksgiving turkey 火雞 感恩節|
3|1F414|🐔|chicken|雞|animal bird ornithology chicken 雞 動物|
3|1F413|🐓|rooster|公雞|animal bird ornithology rooster 公雞 動物 雞|
3|1F423|🐣|hatching chick|小雞破蛋|animal baby bird chick egg hatching hatching chick hatching_chick 小雞破蛋 孵化|
3|1F424|🐤|baby chick|小雞的臉|animal baby bird chick ornithology baby chick baby_chick 小雞的臉 小雞|
3|1F425|🐥|front-facing baby chick|小雞|animal baby bird chick front-facing newborn ornithology front-facing baby chick hatched_chick 小雞 正面小雞|
3|1F426|🐦️|bird|鳥|animal ornithology bird 鳥 動物 鳥類學|
3|1F427|🐧|penguin|企鵝|animal antarctica bird ornithology penguin 企鵝 南極|
3|1F54A|🕊️|dove|飛鳥|bird fly ornithology peace dove 飛鳥 和平鴿 鳥 鴿子|
3|1F985|🦅|eagle|老鷹|animal bird ornithology eagle 老鷹 動物 鳥|
3|1F986|🦆|duck|鴨子|animal bird ornithology duck 鴨子 鳥 鴨|
3|1F9A2|🦢|swan|天鵝|animal bird cygnet duckling ornithology ugly swan 天鵝 動物 小天鵝 醜小鴨 鳥|
3|1F989|🦉|owl|貓頭鷹|animal bird ornithology wise owl 貓頭鷹 動物 智慧 有智慧 貓頭應 鳥|
3|1F9A4|🦤|dodo|渡渡鳥|animal bird extinction large ornithology dodo 渡渡鳥 動物 巨鳥 模里西斯 絕種 鳥|
3|1FAB6|🪶|feather|羽毛|bird flight light plumage feather 羽毛 輕 飄 飛|
3|1F9A9|🦩|flamingo|紅鶴|animal bird flamboyant ornithology tropical flamingo 紅鶴 熱帶 鮮豔|
3|1F99A|🦚|peacock|孔雀|animal bird colorful ornithology ostentatious peahen pretty proud peacock 孔雀 動物 孔雀開屏 賣弄 雌孔雀 驕傲 鳥|
3|1F99C|🦜|parrot|鸚鵡|animal bird ornithology pirate talk parrot 鸚鵡 海盜 說話 講話 鳥|
3|1FABD|🪽|wing|翅膀|angelic ascend aviation bird fly flying heavenly mythology soar wing 翅膀 天使 神話 翼，翅膀，天使，升天，天堂，飛，神話，飛行 飛翔 飛行 鳥|
3|1F426-200D-2B1B|🐦‍⬛|black bird|黑鳥|animal beak bird black caw corvid crow ornithology raven rook black bird black_bird 黑鳥 渡鴉 烏鴉 烏鴉，喙，渡鴉，黑色，鴉叫聲，呱呱，鳥，動物 白嘴鴉 鳥 黑|
3|1FABF|🪿|goose|鵝|animal bird duck flock fowl gaggle gander geese honk ornithology silly goose 鵝 傻瓜 呆子 家禽 愚蠢 糊塗 飛禽 鳥 鵝叫聲 鵝，傻，鴨，雄鵝，鵝群，鴨群，家禽，鳥，動物|
3|1F426-200D-1F525|🐦‍🔥|phoenix|鳳凰|ascend ascension emerge fantasy firebird glory immortal rebirth reincarnation reinvent renewal revival revive rise transform phoenix 鳳凰 上升 不死 幻想 榮耀 浴火重生 火鳥 燃燒 轉變 重生|
3|1F438|🐸|frog|青蛙|animal face frog 青蛙 蛙|
"""

private const val EMOJI_ROWS_5 = """
3|1F40A|🐊|crocodile|鱷魚|animal zoo crocodile 鱷魚 動物 鱷|
3|1F422|🐢|turtle|烏龜|animal terrapin tortoise turtle 烏龜 龜|
3|1F98E|🦎|lizard|蜥蜴|animal reptile lizard 蜥蜴 爬行動物|
3|1F40D|🐍|snake|蛇|animal bearer ophiuchus serpent zodiac snake 蛇 蛇夫座 青蛇|
3|1F432|🐲|dragon face|龍頭|animal dragon face fairy fairytale tale dragon face dragon_face 龍頭 動物 神話故事 龍|
3|1F409|🐉|dragon|龍|animal fairy fairytale knights tale dragon 龍 中國龍 動物 權力遊戲|
3|1F995|🦕|sauropod|蜥腳類恐龍|brachiosaurus brontosaurus dinosaur diplodocus sauropod 蜥腳類恐龍 恐龍 梁龍 長頸巨龍 雷龍|
3|1F996|🦖|T-Rex|暴龍|dinosaur rex t t-rex tyrannosaurus 暴龍 恐龍 雷克斯暴龍 霸王龍|
3|1F433|🐳|spouting whale|鯨魚|animal beach face ocean spouting whale spouting whale 鯨魚 噴水 鯨魚噴水|
3|1F40B|🐋|whale|藍鯨|animal beach ocean whale whale2 藍鯨 鯨|
3|1F42C|🐬|dolphin|海豚|animal beach flipper ocean dolphin 海豚 動物|
3|1FACD|🫍|orca|虎鯨|marine ocean whale orca 虎鯨 大洋 大海 海洋 鯨 鯨魚|
3|1F9AD|🦭|seal|海豹|animal lion ocean sea seal 海豹 動物 海洋 海獅|
3|1F41F|🐟️|fish|魚|animal dinner fishes fishing pisces zodiac fish 魚 動物 雙魚座|
3|1F420|🐠|tropical fish|熱帶魚|animal fish fishes tropical tropical fish tropical_fish 熱帶魚 魚|
3|1F421|🐡|blowfish|河豚|animal fish blowfish 河豚 魚|
3|1F988|🦈|shark|鯊魚|animal fish shark 鯊魚 動物 魚|
3|1F419|🐙|octopus|章魚|animal creature ocean octopus 章魚 八爪魚|
3|1F41A|🐚|spiral shell|海螺|animal beach conch sea shell spiral spiral shell 海螺 動物 螺旋貝殼 貝殼|
3|1FAB8|🪸|coral|珊瑚|change climate ocean reef sea coral 珊瑚 氣候變遷 海 海洋 珊瑚礁 礁 礁石|
3|1FABC|🪼|jellyfish|水母|animal aquarium burn invertebrate jelly life marine ocean ouch plankton sea sting stinger tentacles jellyfish 水母 好痛 果凍 果醬 水母，海洋，觸鬚，蜉蝣生物，水族館，海，海洋，海洋生物，螫，動物 海生 灼痛感 無脊椎動物 蜇人 針刺|
3|1F980|🦀|crab|螃蟹|cancer zodiac crab 螃蟹 巨蟹座 紅蟳 蟹|
3|1F99E|🦞|lobster|龍蝦|animal bisque claws seafood lobster 龍蝦 海鮮 海鮮濃湯 濃湯 爪 紅龍蝦 鉗|
3|1F990|🦐|shrimp|蝦子|food shellfish small shrimp 蝦子 甲殼 蝦 食物|
3|1F991|🦑|squid|魷魚|animal food mollusk squid 魷魚 軟體動物 食物|
3|1F9AA|🦪|oyster|牡蠣|diving pearl oyster 牡蠣 潛水 珍珠|
3|1F40C|🐌|snail|蝸牛|animal escargot garden nature slug snail 蝸牛 蝸牛|
3|1F98B|🦋|butterfly|蝴蝶|insect pretty butterfly 蝴蝶 昆蟲 美|
3|1F41B|🐛|bug|毛毛蟲|animal garden insect bug 毛毛蟲 毛蟲|
3|1F41C|🐜|ant|螞蟻|animal garden insect ant 螞蟻 動物 蟻|
3|1F41D|🐝|honeybee|蜜蜂|animal bee bumblebee honey insect nature spring honeybee 蜜蜂 動物 春天 蜂|
3|1FAB2|🪲|beetle|甲蟲|animal bug insect beetle 甲蟲 動物 昆蟲 蟲 金龜子|
3|1F41E|🐞|lady beetle|瓢蟲|animal beetle garden insect lady ladybird ladybug nature lady beetle lady_beetle 瓢蟲 昆蟲|
3|1F997|🦗|cricket|蟋蟀|animal bug grasshopper insect orthoptera cricket 蟋蟀 直翅目 蚱蜢 蟲子|
3|1FAB3|🪳|cockroach|蟑螂|animal insect pest roach cockroach 蟑螂 動物 噁心 小強 昆蟲|
3|1F577|🕷️|spider|蜘蛛|animal insect spider 蜘蛛 蜘蛛|
3|1F578|🕸️|spider web|蜘蛛網|spider web spider web spider_web 蜘蛛網 網狀 蛛網 蜘蛛|
3|1F982|🦂|scorpion|蠍子|scorpio scorpius zodiac scorpion 蠍子 天蠍座 蠍|
3|1F99F|🦟|mosquito|蚊子|bite disease fever insect malaria pest virus mosquito 蚊子 疾病 病毒 瘧疾 發燒 蟲 蟲咬 蟲子|
3|1FAB0|🪰|fly|蒼蠅|animal disease insect maggot pest rotting fly 蒼蠅 動物 昆蟲 腐爛 舌蠅 馬蠅|
3|1FAB1|🪱|worm|蠕蟲|animal annelid earthworm parasite worm 蠕蟲 動物 寄生蟲 環節動物 蚯蚓 蟲|
3|1F9A0|🦠|microbe|微生物|amoeba bacteria science virus microbe 微生物 濾過性病毒 病毒 細菌 阿米巴|
3|1F490|💐|bouquet|花束|anniversary birthday date flower love plant romance bouquet 花束 浪漫 週年 鮮花|
3|1F338|🌸|cherry blossom|櫻花|blossom cherry flower plant spring springtime cherry blossom cherry_blossom 櫻花 花|
3|1F4AE|💮|white flower|白花|flower white white flower white_flower 白花 花|
3|1FAB7|🪷|lotus|蓮花|beauty buddhism calm flower hinduism peace purity serenity lotus 蓮花 佛教 印度 印度教 寧靜 平和 平靜 純潔 花 越南|
3|1F3F5|🏵️|rosette|花朵|plant rosette 花朵 玫瑰花圖案 花|
3|1F339|🌹|rose|玫瑰|beauty elegant flower love plant red valentine rose 玫瑰 紅玫瑰 花|
3|1F940|🥀|wilted flower|枯萎花朵|dying flower wilted wilted flower wilted_flower 枯萎花朵 凋零 枯萎 花|
3|1F33A|🌺|hibiscus|芙蓉|flower plant hibiscus 芙蓉 花|
3|1F33B|🌻|sunflower|向日葵|flower outdoors plant sun sunflower 向日葵 花|
3|1F33C|🌼|blossom|開花|buttercup dandelion flower plant blossom 開花 花 蒲公英|
3|1F337|🌷|tulip|鬱金香|blossom flower growth plant tulip 鬱金香 花|
3|1FABB|🪻|hyacinth|風信子|bloom bluebonnet flower indigo lavender lilac lupine plant purple shrub snapdragon spring violet hyacinth 風信子 矢車菊 羽扇豆 花 薰衣草 金魚藻 風信子，紫色，花苞，春天，紫羅蘭，靛藍，紫丁香，薰衣草，植物，花，羽扇豆 魯冰花|
3|1F331|🌱|seedling|苗|plant sapling sprout young seedling 苗 幼苗 發芽|
3|1FAB4|🪴|potted plant|盆景|decor grow house nurturing plant pot potted potted plant potted_plant 盆景 室內植物 植物 生長 盆栽 裝飾 觀賞|
3|1F332|🌲|evergreen tree|常青樹|christmas evergreen forest pine tree evergreen tree evergreen_tree 常青樹 松樹 樹 聖誕樹|
3|1F333|🌳|deciduous tree|落葉樹|deciduous forest green habitat shedding tree deciduous tree deciduous_tree 落葉樹 樹|
3|1F334|🌴|palm tree|棕櫚樹|beach palm plant tree tropical palm tree palm_tree 棕櫚樹 樹 熱帶|
3|1F335|🌵|cactus|仙人掌|desert drought nature plant cactus 仙人掌 乾旱 多肉植物 沙漠|
3|1F33E|🌾|sheaf of rice|水稻|ear grain grains plant rice sheaf sheaf of rice ear_of_rice 水稻 稻子 穀物 米|
3|1F33F|🌿|herb|草藥|leaf plant herb 草藥 植物 葉子 香草|
3|2618|☘️|shamrock|三葉草|irish plant shamrock 三葉草 愛爾蘭 草|
3|1F340|🍀|four leaf clover|幸運草|4 clover four four-leaf irish leaf lucky plant four leaf clover four_leaf_clover 幸運草 四 四葉草 愛爾蘭|
3|1F341|🍁|maple leaf|楓葉|falling leaf maple maple leaf maple_leaf 楓葉 紅葉 落葉|
3|1F342|🍂|fallen leaf|落葉|autumn fall fallen falling leaf fallen leaf fallen_leaf 落葉 枯葉 秋葉|
3|1F343|🍃|leaf fluttering in wind|隨風飄落的葉子|blow flutter fluttering leaf wind leaf fluttering in wind leaves 隨風飄落的葉子 風吹葉落|
3|1FAB9|🪹|empty nest|空巢|branch empty home nest nesting empty nest empty_nest 空巢 家 樹枝 築巢 鳥巢|
3|1FABA|🪺|nest with eggs|有蛋的巢|bird branch egg eggs nest nesting nest with eggs nest_with_eggs 有蛋的巢 樹枝 築巢 蛋 鳥 鳥巢|
3|1F344|🍄|mushroom|蘑菇|fungus toadstool mushroom 蘑菇 蕈類 香菇|
3|1FABE|🪾|leafless tree|沒有葉子的樹|bare barren branches dead drought leafless tree trunk winter wood leafless tree 沒有葉子的樹 乾旱 光禿禿的樹 冬天 貧瘠|
4|1F347|🍇|grapes|葡萄|dionysus fruit grape grapes 葡萄 水果|
4|1F348|🍈|melon|瓜|cantaloupe fruit melon 瓜 哈密瓜 水果 甜瓜 蜜瓜 香瓜|
4|1F349|🍉|watermelon|西瓜|fruit watermelon 西瓜 水果|
4|1F34A|🍊|tangerine|橘子|c citrus fruit nectarine orange vitamin tangerine mandarin 橘子 水果|
4|1F34B|🍋|lemon|檸檬|citrus fruit sour lemon 檸檬 水果|
4|1F34B-200D-1F7E9|🍋‍🟩|lime|萊姆|acidity citrus cocktail fruit garnish key margarita mojito refreshing salsa sour tangy tequila tropical zest lime 萊姆 果汁 柑橘、水果、熱帶 柑橘類 檸檬 水果 清爽 熱帶 維他命 c 酸 雞尾酒 食物|
4|1F34C|🍌|banana|香蕉|fruit potassium banana 香蕉 水果 鉀|
4|1F34D|🍍|pineapple|鳳梨|colada fruit pina tropical pineapple 鳳梨 水果 熱帶水果|
4|1F96D|🥭|mango|芒果|food fruit tropical mango 芒果 水果 熱帶|
4|1F34E|🍎|red apple|紅蘋果|apple diet food fruit health red ripe red apple 紅蘋果 水果 蘋果|
4|1F34F|🍏|green apple|青蘋果|apple fruit green green apple green_apple 青蘋果 水果 蘋果|
4|1F350|🍐|pear|梨子|fruit pear 梨子 水果|
4|1F351|🍑|peach|桃子|fruit peach 桃子 水果|
4|1F352|🍒|cherries|櫻桃|berries cherry fruit red cherries 櫻桃 水果|
4|1F353|🍓|strawberry|草莓|berry fruit strawberry 草莓 水果|
4|1FAD0|🫐|blueberries|藍莓|berries berry bilberry blue blueberry food fruit blueberries 藍莓 水果 漿果 莓 莓果 食物|
4|1F95D|🥝|kiwi fruit|奇異果|food fruit kiwi kiwi fruit kiwi_fruit 奇異果 水果 食物|
4|1F345|🍅|tomato|番茄|food fruit vegetable tomato 番茄 水果 蔬果|
4|1FAD2|🫒|olive|橄欖|food olive 橄欖 食物|
4|1F965|🥥|coconut|椰子|colada palm piña coconut 椰子 棕櫚 鳳梨椰汁蘭姆酒|
4|1F951|🥑|avocado|酪梨|food fruit avocado 酪梨 水果 食物|
4|1F346|🍆|eggplant|茄子|aubergine vegetable eggplant 茄子 蔬菜|
4|1F954|🥔|potato|馬鈴薯|food vegetable potato 馬鈴薯 根莖類 蔬菜 食物|
4|1F955|🥕|carrot|胡蘿蔔|food vegetable carrot 胡蘿蔔 根莖類 紅蘿蔔 蔬菜 食物|
4|1F33D|🌽|ear of corn|玉米|corn crops ear farm maize maze ear of corn 玉米 蔬菜|
4|1F336|🌶️|hot pepper|辣椒|hot pepper hot pepper hot_pepper 辣椒 蔬菜|
4|1FAD1|🫑|bell pepper|甜椒|bell capsicum food pepper vegetable bell pepper bell_pepper 甜椒 蔬菜 辣椒 青椒 食物|
4|1F952|🥒|cucumber|小黃瓜|food pickle vegetable cucumber 小黃瓜 蔬菜 食物 黃瓜|
4|1F96C|🥬|leafy green|綠葉蔬菜|bok burgers cabbage choy green kale leafy lettuce salad leafy green leafy_green 綠葉蔬菜 小白菜 沙拉 甘藍菜 綠色葉菜 羽衣甘藍 萵苣 高麗菜|
4|1F966|🥦|broccoli|花椰菜|cabbage wild broccoli 花椰菜 綠花椰菜 西蘭花 青花菜|
4|1F9C4|🧄|garlic|大蒜|flavoring garlic 大蒜 調味 調味料|
4|1F9C5|🧅|onion|洋蔥|flavoring onion 洋蔥 調味 調味料|
4|1F95C|🥜|peanuts|花生|food nut peanut vegetable peanuts 花生 堅果 蔬菜 食物|
4|1FAD8|🫘|beans|豆子|food kidney legume small beans 豆子 小 腰子 豆 豆類 食物|
4|1F330|🌰|chestnut|栗子|almond plant chestnut 栗子 杏仁|
4|1FADA|🫚|ginger root|薑|beer ginger health herb natural root spice ginger root ginger_root 薑 啤酒 根 薑，根，香料，香草，大自然，健康，啤酒 調味品 辛辣 香料|
4|1FADB|🫛|pea pod|豌豆莢|beans beanstalk edamame legume pea pod soybean vegetable veggie pea pod pea_pod 豌豆莢 毛豆 蔬菜 豆子 豆子，豆莢，毛豆，蔬菜，豆莖，菜，黃豆，豆類，豆 豆莢 豆類 豌豆|
4|1F344-200D-1F7EB|🍄‍🟫|brown mushroom|棕色蕈菇|food fungi fungus mushroom nature pizza portobello shiitake shroom spore sprout toppings truffle vegetable vegetarian veggie brown mushroom 棕色蕈菇 松露 素食 菇 蕈類 蘑菇 食物 食物、真菌、自然、蔬菜 香菇|
4|1FADC|🫜|root vegetable|根莖類蔬菜|beet food garden radish root salad turnip vegetable vegetarian root vegetable 根莖類蔬菜 根 甜菜 花園 蔬菜 蘿蔔|
4|1F35E|🍞|bread|吐司|carbs food grain loaf restaurant toast wheat bread 吐司 全穀物 醣類 麵包|
4|1F950|🥐|croissant|可頌|bread breakfast crescent food french roll croissant 可頌 法式 牛角麵包 麵包|
4|1F956|🥖|baguette bread|法國麵包|baguette bread food french baguette bread baguette_bread 法國麵包 法式 食物 麵包|
4|1FAD3|🫓|flatbread|麵餅|arepa bread food gordita lavash naan pita flatbread 麵餅 印度南餅 圓麵餅 扁麵包 玉米麵包 糕餅 薄麵包 食物 餅 麵包|
4|1F968|🥨|pretzel|蝴蝶餅|convoluted twisted pretzel 蝴蝶餅 彎曲 椒鹽卷餅 椒鹽捲餅 盤繞 糾結|
4|1F96F|🥯|bagel|貝果|bakery bread breakfast schmear bagel 貝果 早餐 硬麵包圈 醬 麵包|
4|1F95E|🥞|pancakes|鬆餅|breakfast crêpe food hotcake pancake pancakes 鬆餅 煎餅 薄餅 食物|
4|1F9C7|🧇|waffle|格子鬆餅|breakfast indecisive iron waffle 格子鬆餅 早餐 楓糖 無法決定 猶豫不決 鐵 鬆餅|
4|1F9C0|🧀|cheese wedge|起士|cheese wedge cheese wedge 起士 乳酪|
4|1F356|🍖|meat on bone|排骨|bone meat meat on bone meat_on_bone 排骨 帶骨肉|
4|1F357|🍗|poultry leg|雞腿|bone chicken drumstick hungry leg poultry turkey poultry leg poultry_leg 雞腿 帶骨肉|
4|1F969|🥩|cut of meat|肉片|chop cut lambchop meat porkchop red steak cut of meat cut_of_meat 肉片 牛扒 牛排 紅肉 羊排 肉類 豬排|
4|1F953|🥓|bacon|培根|breakfast food meat bacon 培根 肉類 食物|
4|1F354|🍔|hamburger|漢堡|burger eat fast food hungry hamburger 漢堡 漢堡包 餓了|
4|1F35F|🍟|french fries|薯條|fast food french fries french fries 薯條 炸薯條 速食|
4|1F355|🍕|pizza|披薩|cheese food hungry pepperoni slice pizza 披薩 義大利辣肉腸 起士披薩|
4|1F32D|🌭|hot dog|熱狗|dog frankfurter hot hotdog sausage hot dog 熱狗 熱狗堡 香腸|
4|1F96A|🥪|sandwich|三明治|bread sandwich 三明治 麵包|
4|1F32E|🌮|taco|夾餅|mexican taco 夾餅 墨西哥夾餅|
4|1F32F|🌯|burrito|捲餅|mexican wrap burrito 捲餅 墨西哥捲餅|
4|1FAD4|🫔|tamale|玉米粉蒸肉|food mexican pamonha wrapped tamale 玉米粉蒸肉 墨西哥 墨西哥粽 捲餅 食物|
4|1F959|🥙|stuffed flatbread|薄捲餅|falafel flatbread food gyro kebab stuffed stuffed flatbread stuffed_flatbread 薄捲餅 炸豆丸子 食物|
4|1F9C6|🧆|falafel|油炸鷹嘴豆餅|chickpea meatball falafel 油炸鷹嘴豆餅 肉丸 雪蓮子 鷹嘴豆|
4|1F95A|🥚|egg|蛋|breakfast food egg 蛋 雞蛋 食物|
4|1F373|🍳|cooking|煎蛋|breakfast easy egg fry frying over pan restaurant side sunny up cooking fried_egg 煎蛋 荷包蛋|
4|1F958|🥘|shallow pan of food|淺鍋料理|casserole food paella pan shallow shallow pan of food shallow_pan_of_food 淺鍋料理 平底鍋 料理 法國砂鍋 淺鍋 烤鍋 烤鍋料理|
4|1F372|🍲|pot of food|火鍋|food pot soup stew pot of food 火鍋 燉菜|
4|1FAD5|🫕|fondue|涮製菜餚|cheese chocolate food melted pot ski fondue 涮製菜餚 乳酪 巧克力 滑雪 瑞士 融化 起司 起司火鍋 鍋 食物|
4|1F963|🥣|bowl with spoon|碗和湯匙|bowl breakfast cereal congee oatmeal porridge spoon bowl with spoon bowl_with_spoon 碗和湯匙 早餐 早餐穀物 湯匙與碗 燕麥 粥 餐具|
4|1F957|🥗|green salad|生菜沙拉|food green salad green salad green_salad 生菜沙拉 沙拉 生菜 食物|
4|1F37F|🍿|popcorn|爆米花|corn movie pop popcorn 爆米花 看電影|
4|1F9C8|🧈|butter|奶油|dairy butter 奶油 乳製品|
4|1F9C2|🧂|salt|鹽|condiment flavor mad salty shaker taste upset salt 鹽 佐料 火大 調味品 調味瓶|
4|1F96B|🥫|canned food|罐頭食品|can canned food canned food canned_food 罐頭食品 罐頭 罐頭食物|
4|1F371|🍱|bento box|便當|bento box food bento box 便當 餐盒|
4|1F358|🍘|rice cracker|米果|cracker food rice rice cracker rice_cracker 米果 仙貝 米食|
4|1F359|🍙|rice ball|飯糰|ball food japanese rice rice ball rice_ball 飯糰 飯糰|
4|1F35A|🍚|cooked rice|米飯|cooked food rice cooked rice 米飯 煮熟的米飯 飯|
4|1F35B|🍛|curry rice|咖哩飯|curry food rice curry rice 咖哩飯 飯|
4|1F35C|🍜|steaming bowl|湯麵|bowl chopsticks food noodle pho ramen soup steaming steaming bowl 湯麵 熱麵碗 筷子 麵|
4|1F35D|🍝|spaghetti|義大利麵|food meatballs pasta restaurant spaghetti 義大利麵 肉醬麵 麵|
4|1F360|🍠|roasted sweet potato|烤地瓜|food potato roasted sweet roasted sweet potato sweet_potato 烤地瓜 地瓜|
4|1F362|🍢|oden|關東煮|food kebab restaurant seafood skewer stick oden 關東煮 海鮮串 烤肉串|
4|1F363|🍣|sushi|壽司|food sushi 壽司 壽司|
4|1F364|🍤|fried shrimp|天婦羅|fried prawn shrimp tempura fried shrimp fried_shrimp 天婦羅 炸蝦 蝦|
4|1F365|🍥|fish cake with swirl|魚板|cake fish food pastry restaurant swirl fish cake with swirl fish_cake 魚板 魚板|
4|1F96E|🥮|moon cake|月餅|autumn cake festival moon yuèbǐng moon cake moon_cake 月餅 中秋 中秋節 秋天|
4|1F361|🍡|dango|糥米丸子|dessert japanese skewer stick sweet dango 糥米丸子 丸子串 糥米丸串|
4|1F95F|🥟|dumpling|水餃|empanada gyōza jiaozi pierogi potsticker dumpling 水餃 餃子|
4|1F960|🥠|fortune cookie|幸運餅乾|cookie fortune prophecy fortune cookie fortune_cookie 幸運餅乾 簽餅 語言|
4|1F961|🥡|takeout box|外帶餐盒|box chopsticks delivery food oyster pail takeout takeout box takeout_box 外帶餐盒 中市外賣盒 外賣 筷子 送便當 送餐 飯盒|
4|1F366|🍦|soft ice cream|霜淇淋|cream dessert food ice icecream restaurant serve soft sweet soft ice cream 霜淇淋 冰品 冰淇淋|
4|1F367|🍧|shaved ice|刨冰|dessert ice restaurant shaved sweet shaved ice shaved_ice 刨冰 冰品 刨|
4|1F368|🍨|ice cream|冰淇淋|cream dessert food ice restaurant sweet ice cream ice_cream 冰淇淋 冰品 甜品|
4|1F369|🍩|doughnut|甜甜圈|breakfast dessert donut food sweet doughnut 甜甜圈 甜點|
4|1F36A|🍪|cookie|餅乾|chip chocolate dessert sweet cookie 餅乾 巧克力餅乾 甜點|
4|1F382|🎂|birthday cake|生日蛋糕|bday birthday cake celebration dessert happy pastry sweet birthday cake 生日蛋糕 慶祝 生日 生日快樂 蛋糕|
4|1F370|🍰|shortcake|蛋糕|cake dessert pastry slice sweet shortcake 蛋糕 甜點|
4|1F9C1|🧁|cupcake|杯子蛋糕|bakery dessert sprinkles sugar sweet treat cupcake 杯子蛋糕 杯蛋糕 烘焙 甜點 糕點|
4|1F967|🥧|pie|派|apple filling fruit meat pastry pumpkin slice pie 派 一片派 南瓜派 糕點 肉派 蘋果派 西點 餡|
4|1F36B|🍫|chocolate bar|巧克力|bar candy chocolate dessert halloween sweet tooth chocolate bar chocolate_bar 巧克力 巧克力棒 巧克力磚|
4|1F36C|🍬|candy|糖|cavities dessert halloween restaurant sweet tooth wrapper candy 糖 糖果 糖果紙 萬聖節|
4|1F36D|🍭|lollipop|棒棒糖|candy dessert food restaurant sweet lollipop 棒棒糖 糖果|
4|1F36E|🍮|custard|卡士達|dessert pudding sweet custard 卡士達 布丁 甜點|
4|1F36F|🍯|honey pot|蜂蜜|barrel bear food honey honeypot jar pot sweet honey pot honey_pot 蜂蜜 甜點 蜂蜜罐|
4|1F37C|🍼|baby bottle|奶瓶|babies baby birth born bottle drink infant milk newborn baby bottle baby_bottle 奶瓶 牛奶|
4|1F95B|🥛|glass of milk|一杯牛奶|drink glass milk glass of milk milk_glass 一杯牛奶 杯 牛奶 飲料|
4|2615|☕️|hot beverage|熱飲|beverage cafe caffeine chai coffee drink hot morning steaming tea hot beverage 熱飲 咖啡 星巴克 茶 飲料|
4|1FAD6|🫖|teapot|茶壺|brew drink food pot tea teapot 茶壺 壺 泡茶 茶 食物 飲品|
4|1F375|🍵|teacup without handle|熱茶|beverage cup drink handle oolong tea teacup teacup without handle 熱茶 無柄茶杯 茶 茶杯|
4|1F376|🍶|sake|清酒|bar beverage bottle cup drink restaurant sake 清酒 喝清酒 酒|
4|1F37E|🍾|bottle with popping cork|洋酒|bar bottle cork drink popping bottle with popping cork champagne 洋酒 酒|
4|1F377|🍷|wine glass|葡萄酒|alcohol bar beverage booze club drink drinking drinks glass restaurant wine wine glass wine_glass 葡萄酒 紅酒 酒 酒杯|
4|1F378|🍸️|cocktail glass|雞尾酒|alcohol bar booze club cocktail drink drinking drinks glass mad martini men cocktail glass 雞尾酒 酒 酒杯 馬提尼|
4|1F379|🍹|tropical drink|熱帶水果飲料|alcohol bar booze club cocktail drink drinking drinks drunk mai party tai tropical tropics tropical drink tropical_drink 熱帶水果飲料 果汁 熱帶水果果汁|
4|1F37A|🍺|beer mug|啤酒|alcohol ale bar beer booze drink drinking drinks mug octoberfest oktoberfest pint stein summer beer mug 啤酒 啤酒節 酒|
4|1F37B|🍻|clinking beer mugs|乾杯吧|alcohol bar beer booze bottoms cheers clink clinking drinking drinks mugs clinking beer mugs beers 乾杯吧 乾杯 碰杯 酒|
4|1F942|🥂|clinking glasses|乾杯|celebrate clink clinking drink glass glasses clinking glasses clinking_glasses 乾杯 慶祝 碰杯 舉杯|
4|1F943|🥃|tumbler glass|威士忌杯|glass liquor scotch shot tumbler whiskey whisky tumbler glass tumbler_glass 威士忌杯 威士忌 烈酒 玻璃杯 酒杯|
4|1FAD7|🫗|pouring liquid|倒出液體|accident drink empty glass liquid oops pour pouring spill water pouring liquid pouring_liquid 倒出液體 倒 倒空 哎呀 意外 水 流出 流空 灑出 玻璃杯|
4|1F964|🥤|cup with straw|杯子和吸管|cup drink juice malt soda soft straw water cup with straw cup_with_straw 杯子和吸管 吸管杯 果汁 水 汽水 飲料 麥芽飲料|
4|1F9CB|🧋|bubble tea|珍珠奶茶|boba bubble food milk pearl tea bubble tea bubble_tea 珍珠奶茶 台灣 手搖 珍奶 珍珠 茶 食物 飲料|
4|1F9C3|🧃|beverage box|鋁箔包|beverage box juice straw sweet beverage box beverage_box 鋁箔包 吸管 果汁 甜 飲料|
4|1F9C9|🧉|mate|瑪黛茶|drink mate 瑪黛茶 飲料|
4|1F9CA|🧊|ice|冰塊|cold cube iceberg ice ice_cube 冰塊 冰 冰山 冷|
4|1F962|🥢|chopsticks|筷子|hashi jeotgarak kuaizi chopsticks 筷子 筷子|
4|1F37D|🍽️|fork and knife with plate|餐具|cooking dinner eat fork knife plate fork and knife with plate plate_with_cutlery 餐具 刀叉餐盤 餐盤|
4|1F374|🍴|fork and knife|刀叉|breakfast breaky cooking cutlery delicious dinner eat feed food fork hungry knife lunch restaurant yum yummy fork and knife fork_and_knife 刀叉 午餐 吃 吃飯 好吃 早餐 晚餐 美味 食物 餐廳|
4|1F944|🥄|spoon|湯匙|eat tableware spoon 湯匙 吃 餐具|
4|1F52A|🔪|kitchen knife|菜刀|chef cooking hocho kitchen knife tool weapon kitchen knife 菜刀 刀|
4|1FAD9|🫙|jar|廣口瓶|condiment container empty nothing sauce store jar 廣口瓶 容器 果醬 沒有東西 空瓶 空的 罐子 調味品 貯存品|
4|1F3FA|🏺|amphora|陶罐|aquarius cooking drink jug tool weapon zodiac amphora 陶罐 容器|
5|1F30D|🌍️|globe showing Europe-Africa|歐洲及非洲|africa earth europe europe-africa globe showing world globe showing europe-africa earth_africa 歐洲及非洲 地球 歐洲 歐非 非洲|
5|1F30E|🌎️|globe showing Americas|美洲|americas earth globe showing world globe showing americas earth_americas 美洲 地球|
5|1F30F|🌏️|globe showing Asia-Australia|亞洲及澳洲|asia asia-australia australia earth globe showing world globe showing asia-australia earth_asia 亞洲及澳洲 亞洲 亞澳 地球 澳洲|
5|1F310|🌐|globe with meridians|子午線|earth globe internet meridians web world worldwide globe with meridians globe_with_meridians 子午線 地球|
5|1F5FA|🗺️|world map|世界地圖|map world world map world_map 世界地圖 世界 地圖|
5|1F5FE|🗾|map of Japan|日本|japan map map of japan 日本 日本列島|
5|1F9ED|🧭|compass|指南針|direction magnetic navigation orienteering compass 指南針 定向 導航 方向 磁鐵 羅盤|
5|1F3D4|🏔️|snow-capped mountain|雪山|cold mountain snow snow-capped snow-capped mountain mountain_snow 雪山 雪峰|
5|26F0|⛰️|mountain|山|mountain 山 山峰|
5|1F6D8|🛘|landslide|山崩|avalanche danger disaster earthquake mountain mudslide rocks landslide 山崩 危險 土石流 地震 山 岩石 災難 雪崩|
5|1F30B|🌋|volcano|火山|eruption mountain nature volcano 火山 火山爆發|
5|1F5FB|🗻|mount fuji|富士山|fuji mount mountain nature mount fuji mount_fuji 富士山 山峰|
5|1F3D5|🏕️|camping|露營|camping 露營 帳篷露營 露營帳篷|
5|1F3D6|🏖️|beach with umbrella|海灘陽傘|beach umbrella beach with umbrella beach_umbrella 海灘陽傘 海灘|
5|1F3DC|🏜️|desert|沙漠|desert 沙漠 沙漠|
5|1F3DD|🏝️|desert island|熱帶小島|desert island desert island desert_island 熱帶小島 沙漠 沙灘小島|
5|1F3DE|🏞️|national park|國家公園|national park national park national_park 國家公園 公園|
5|1F3DF|🏟️|stadium|競技場|stadium 競技場 球場 運動場 體育場 體育館|
5|1F3DB|🏛️|classical building|古典建築|building classical classical building classical_building 古典建築 古蹟|
5|1F3D7|🏗️|building construction|施工中|building construction crane building construction building_construction 施工中 施工|
5|1F9F1|🧱|brick|磚塊|bricks clay mortar wall brick 磚塊 泥土 灰泥 灰漿 牆 牆壁 磚|
5|1FAA8|🪨|rock|石頭|boulder heavy solid stone tough rock 石頭 岩石 巨石 巨礫 石材 硬|
5|1FAB5|🪵|wood|木頭|log lumber timber wood 木頭 木塊 木料 木材|
5|1F6D6|🛖|hut|小屋|home house roundhouse shelter yurt hut 小屋 圓頂帳篷 家 房屋 茅屋|
5|1F3D8|🏘️|houses|房屋建築|house houses 房屋建築 屋舍|
5|1F3DA|🏚️|derelict house|廢墟|derelict home house derelict house derelict_house 廢墟 荒宅|
5|1F3E0|🏠️|house|房子|building country heart home ranch settle simple suburban suburbia where house 房子 家 郊區|
5|1F3E1|🏡|house with garden|別墅|building country garden heart home house ranch settle simple suburban suburbia where house with garden house_with_garden 別墅 家 有庭院的家 郊區|
5|1F3E2|🏢|office building|辦公大樓|building city cubical job office office building 辦公大樓 市區 都市 高樓大廈|
5|1F3E3|🏣|Japanese post office|日本郵局|building japanese office post japanese post office post_office 日本郵局 郵局|
5|1F3E4|🏤|post office|歐洲郵局|building european office post post office european_post_office 歐洲郵局 郵局|
5|1F3E5|🏥|hospital|醫院|building doctor medicine hospital 醫院 醫生 醫療 醫藥|
5|1F3E6|🏦|bank|銀行|building bank 銀行 銀行|
5|1F3E8|🏨|hotel|旅館|building hotel 旅館 飯店|
5|1F3E9|🏩|love hotel|賓館|building hotel love love hotel love_hotel 賓館 汽車旅館|
5|1F3EA|🏪|convenience store|便利商店|24 building convenience hours store convenience store convenience_store 便利商店 24 小時便利店|
5|1F3EB|🏫|school|學校|building school 學校 校舍|
5|1F3EC|🏬|department store|百貨公司|building department store department store department_store 百貨公司 購物商場|
5|1F3ED|🏭️|factory|工廠|building factory 工廠 廠房|
5|1F3EF|🏯|Japanese castle|日式城堡|building castle japanese japanese castle japanese_castle 日式城堡 城堡|
5|1F3F0|🏰|castle|歐式城堡|building european castle european_castle 歐式城堡 城堡|
5|1F492|💒|wedding|婚禮|chapel hitched nuptials romance wedding 婚禮 教堂婚禮|
5|1F5FC|🗼|Tokyo tower|東京鐵塔|tokyo tower tokyo tower tokyo_tower 東京鐵塔 鐵塔|
5|1F5FD|🗽|Statue of Liberty|自由女神|liberty new ny nyc statue york statue of liberty statue_of_liberty 自由女神 紐約 自由女神像|
5|26EA|⛪️|church|教堂|bless chapel christian cross religion church 教堂 十字架 基督教|
5|1F54C|🕌|mosque|清真寺|islam masjid muslim religion mosque 清真寺 伊斯蘭建築|
5|1F6D5|🛕|hindu temple|印度廟|hindu temple hindu temple hindu_temple 印度廟 印度 廟|
5|1F54D|🕍|synagogue|猶太教堂|jew jewish judaism religion temple synagogue 猶太教堂 教堂 猶太教|
5|26E9|⛩️|shinto shrine|神社|religion shinto shrine shinto shrine shinto_shrine 神社 宗教 鳥居|
5|1F54B|🕋|kaaba|天房|hajj islam muslim religion umrah kaaba 天房 伊斯蘭建築 朝覲|
5|26F2|⛲️|fountain|噴泉|fountain 噴泉 噴水池|
5|26FA|⛺️|tent|帳篷|camping tent 帳篷 露營|
5|1F301|🌁|foggy|霧|fog foggy 霧 天氣|
5|1F303|🌃|night with stars|星空|night star stars night with stars night_with_stars 星空 夜晚 星夜|
5|1F3D9|🏙️|cityscape|都市風景|city cityscape 都市風景 城市 天際線|
5|1F304|🌄|sunrise over mountains|日出|morning mountains over sun sunrise sunrise over mountains sunrise_over_mountains 日出 黎明|
5|1F305|🌅|sunrise|旭日|morning nature sun sunrise 旭日 朝陽|
5|1F306|🌆|cityscape at dusk|黃昏|at building city cityscape dusk evening landscape sun sunset cityscape at dusk city_sunset 黃昏 建築物 暮色|
5|1F307|🌇|sunset|夕陽|building dusk sun sunset city_sunrise 夕陽 建築物 日落|
5|1F309|🌉|bridge at night|夜橋|at bridge night bridge at night bridge_at_night 夜橋 夜景 橋|
5|2668|♨️|hot springs|溫泉|hot hotsprings springs steaming hot springs 溫泉 泡湯 熱氣|
5|1F3A0|🎠|carousel horse|旋轉木馬|carousel entertainment horse carousel horse carousel_horse 旋轉木馬 旋轉木馬|
5|1F6DD|🛝|playground slide|溜滑梯|amusement park play playground playing slide sliding theme playground slide playground_slide 溜滑梯 主題樂園 玩樂 遊樂場 遊玩|
5|1F3A1|🎡|ferris wheel|摩天輪|amusement ferris park theme wheel ferris wheel ferris_wheel 摩天輪 遊樂區|
5|1F3A2|🎢|roller coaster|雲霄飛車|amusement coaster park roller theme roller coaster roller_coaster 雲霄飛車 雲霄飛車|
5|1F488|💈|barber pole|理髮店|barber cut fresh haircut pole shave barber pole 理髮店 修面 刮鬍 理髮|
5|1F3AA|🎪|circus tent|馬戲團|circus tent circus tent circus_tent 馬戲團 帳篷 馬戲團帳篷|
5|1F682|🚂|locomotive|蒸汽火車|caboose engine railway steam train trains travel locomotive steam_locomotive 蒸汽火車 火車|
5|1F683|🚃|railway car|有軌電車|car electric railway train tram travel trolleybus railway car railway_car 有軌電車 軌道電車|
5|1F684|🚄|high-speed train|高鐵|high-speed railway shinkansen speed train high-speed train bullettrain_side 高鐵 新幹線 火車|
5|1F685|🚅|bullet train|高鐡車頭|bullet high-speed nose railway shinkansen speed train travel bullet train bullettrain_front 高鐡車頭 子彈列車 火車 高鐵|
5|1F686|🚆|train|火車|arrived choo railway train train2 火車 到站 鐵道|
5|1F687|🚇️|metro|地鐵|subway travel metro 地鐵 捷運|
5|1F688|🚈|light rail|輕軌|arrived light monorail rail railway light rail light_rail 輕軌 抵達 捷運 火車 鐵道|
5|1F689|🚉|station|車站|railway train station 車站 捷運 鐵路|
5|1F68A|🚊|tram|路面電車|trolleybus tram 路面電車 捷運 軌道電車|
5|1F69D|🚝|monorail|單軌|vehicle monorail 單軌 火車|
5|1F69E|🚞|mountain railway|山區鐵路|car mountain railway trip mountain railway mountain_railway 山區鐵路 火車|
5|1F68B|🚋|tram car|電纜車|bus car tram trolley trolleybus tram car train 電纜車 電車|
5|1F68C|🚌|bus|公車|school vehicle bus 公車 公共汽車|
5|1F68D|🚍️|oncoming bus|公共汽車|bus cars oncoming oncoming bus oncoming_bus 公共汽車 公車|
5|1F68E|🚎|trolleybus|無軌電車|bus tram trolley trolleybus 無軌電車 公車 電動巴士|
5|1F690|🚐|minibus|小型巴士|bus drive van vehicle minibus 小型巴士 小巴|
5|1F691|🚑️|ambulance|救護車|emergency vehicle ambulance 救護車 救護車|
5|1F692|🚒|fire engine|消防車|engine fire truck fire engine fire_engine 消防車 消防車|
5|1F693|🚓|police car|警車|5–0 car cops patrol police police car police_car 警車 警車|
5|1F694|🚔️|oncoming police car|警察車|car oncoming police oncoming police car oncoming_police_car 警察車 警車|
5|1F695|🚕|taxi|計程車|cab cabbie car drive vehicle yellow taxi 計程車 小黃|
5|1F696|🚖|oncoming taxi|小黃|cab cabbie cars drove hail oncoming taxi yellow oncoming taxi oncoming_taxi 小黃 優步 計程車|
5|1F697|🚗|automobile|汽車|car driving vehicle automobile red_car 汽車 轎車|
5|1F698|🚘️|oncoming automobile|轎車|automobile car cars drove oncoming vehicle oncoming automobile oncoming_automobile 轎車 汽車|
5|1F699|🚙|sport utility vehicle|休旅車|car drive recreational sport sportutility utility vehicle sport utility vehicle blue_car 休旅車 休旅車|
5|1F6FB|🛻|pickup truck|皮卡車|automobile car flatbed pick-up pickup transportation truck pickup truck pickup_truck 皮卡車 交通工具 卡車 敞篷小貨車 汽車 皮卡 貨卡|
5|1F69A|🚚|delivery truck|貨車|car delivery drive truck vehicle delivery truck 貨車 卡車|
5|1F69B|🚛|articulated lorry|卡車|articulated car drive lorry move semi truck vehicle articulated lorry articulated_lorry 卡車 貨車|
5|1F69C|🚜|tractor|拖弋機|vehicle tractor 拖弋機 拖拉機|
5|1F3CE|🏎️|racing car|賽車|car racing zoom racing car racing_car 賽車 賽車|
5|1F3CD|🏍️|motorcycle|機車|racing motorcycle 機車 摩托車|
5|1F6F5|🛵|motor scooter|摩托車|motor scooter motor scooter motor_scooter 摩托車 機車|
5|1F9BD|🦽|manual wheelchair|輪椅|accessibility manual wheelchair manual wheelchair manual_wheelchair 輪椅 行動不便|
5|1F9BC|🦼|motorized wheelchair|電動輪椅|accessibility motorized wheelchair motorized wheelchair motorized_wheelchair 電動輪椅 行動不便|
5|1F6FA|🛺|auto rickshaw|嘟嘟車|auto rickshaw tuk auto rickshaw auto_rickshaw 嘟嘟車 電動式人力車|
5|1F6B2|🚲️|bicycle|自行車|bike class cycle cycling cyclist gang ride spin spinning bicycle 自行車 腳踏車|
5|1F6F4|🛴|kick scooter|滑板車|kick scooter kick scooter kick_scooter 滑板車 滑行|
5|1F6F9|🛹|skateboard|滑板|board skate skater wheels skateboard 滑板 直排輪|
5|1F6FC|🛼|roller skate|輪式溜冰鞋|blades roller skate skates sport roller skate roller_skate 輪式溜冰鞋 單排輪 溜冰 溜冰鞋 滑輪 直排輪 運動|
5|1F68F|🚏|bus stop|公車站|bus busstop stop bus stop 公車站 公車站牌|
5|1F6E3|🛣️|motorway|高速公路|highway road motorway 高速公路 公路 道路|
5|1F6E4|🛤️|railway track|鐵軌|railway track train railway track railway_track 鐵軌 鐵道|
5|1F6E2|🛢️|oil drum|油桶|drum oil oil drum oil_drum 油桶 石油|
5|26FD|⛽️|fuel pump|加油站|diesel fuel fuelpump gas gasoline pump station fuel pump 加油站 加油 加油幫浦|
5|1F6DE|🛞|wheel|輪子|car circle tire turn vehicle wheel 輪子 圓圈 車 車輛 輪胎 轉動|
5|1F6A8|🚨|police car light|警車燈|alarm alert beacon car emergency light police revolving siren police car light rotating_light 警車燈 急救 警察|
5|1F6A5|🚥|horizontal traffic light|紅綠燈|horizontal intersection light signal stop stoplight traffic horizontal traffic light traffic_light 紅綠燈 交通號誌|
5|1F6A6|🚦|vertical traffic light|直式紅綠燈|drove intersection light signal stop stoplight traffic vertical vertical traffic light vertical_traffic_light 直式紅綠燈 交通號誌 十字路口 紅綠燈|
5|1F6D1|🛑|stop sign|停止標誌|octagonal sign stop stop sign stop_sign 停止標誌 停止 八角形 標誌|
5|1F6A7|🚧|construction|施工|barrier construction 施工 工地 施工中|
5|2693|⚓️|anchor|錨|ship tool anchor 錨 船錨|
5|1F6DF|🛟|ring buoy|救生圈|buoy float life lifesaver preserver rescue ring safety save saver swim ring buoy ring_buoy 救生圈 保命工具 安全 救援 救生 救生用具 浮具 浮標 游泳|
5|26F5|⛵️|sailboat|帆船|boat resort sailing sea yacht sailboat 帆船 遊艇|
5|1F6F6|🛶|canoe|獨木舟|boat canoe 獨木舟 船|
5|1F6A4|🚤|speedboat|快艇|billionaire boat lake luxury millionaire summer travel speedboat 快艇 快艇|
5|1F6F3|🛳️|passenger ship|客船|passenger ship passenger ship passenger_ship 客船 客輪 船|
5|26F4|⛴️|ferry|渡輪|boat passenger ferry 渡輪 客船 船|
5|1F6E5|🛥️|motor boat|汽艇|boat motor motorboat motor boat motor_boat 汽艇 船|
5|1F6A2|🚢|ship|船|boat passenger travel ship 船 船|
5|2708|✈️|airplane|飛機|aeroplane fly flying jet plane travel airplane 飛機 噴射機 旅行|
5|1F6E9|🛩️|small airplane|小飛機|aeroplane airplane plane small small airplane small_airplane 小飛機 小型飛機 私人飛機|
5|1F6EB|🛫|airplane departure|飛機起飛|aeroplane airplane check-in departure departures plane airplane departure flight_departure 飛機起飛 出境 登機 起飛 飛機|
5|1F6EC|🛬|airplane arrival|飛機著陸|aeroplane airplane arrival arrivals arriving landing plane airplane arrival flight_arrival 飛機著陸 降落|
5|1FA82|🪂|parachute|降落傘|hang-glide parasail skydive parachute 降落傘 懸掛式滑翔 拖曳傘 滑翔翼 跳傘|
5|1F4BA|💺|seat|座位|chair seat 座位 座椅|
5|1F681|🚁|helicopter|直升機|copter roflcopter travel vehicle helicopter 直升機 直升機|
5|1F69F|🚟|suspension railway|懸索鐵路|railway suspension suspension railway suspension_railway 懸索鐵路 懸掛式單軌鐵路 懸掛鐵路 空鐵|
5|1F6A0|🚠|mountain cableway|纜車|cable cableway gondola lift mountain ski mountain cableway mountain_cableway 纜車 纜車|
5|1F6A1|🚡|aerial tramway|空中纜車|aerial cable car gondola ropeway tramway aerial tramway aerial_tramway 空中纜車 纜車|
5|1F6F0|🛰️|satellite|衛星|space satellite artificial_satellite 衛星 太空|
5|1F680|🚀|rocket|火箭|launch rockets space travel rocket 火箭 火箭|
5|1F6F8|🛸|flying saucer|飛碟|aliens extra flying saucer terrestrial ufo flying saucer flying_saucer 飛碟 ufo 外太空人 外星人 幽浮 異形|
5|1F6CE|🛎️|bellhop bell|服務鈴|bell bellhop hotel bellhop bell bellhop_bell 服務鈴 服務鈴|
5|1F9F3|🧳|luggage|行李|bag packing roller suitcase travel luggage 行李 手提箱 打包 旅行 滑輪行李箱|
5|231B|⌛️|hourglass done|沙漏|done hourglass sand time timer hourglass done 沙漏 沙漏|
5|23F3|⏳️|hourglass not done|流動的沙漏|done flowing hourglass hours not sand timer waiting yolo hourglass not done hourglass_flowing_sand 流動的沙漏 沙漏 等待|
5|231A|⌚️|watch|手錶|clock time watch 手錶 錶|
5|23F0|⏰️|alarm clock|鬧鐘|alarm clock hours hrs late time waiting alarm clock alarm_clock 鬧鐘 時鐘|
5|23F1|⏱️|stopwatch|碼錶|clock time stopwatch 碼錶 時鐘|
5|23F2|⏲️|timer clock|計時器|clock timer timer clock timer_clock 計時器 時鐘|
5|1F570|🕰️|mantelpiece clock|座鐘|clock mantelpiece time mantelpiece clock mantelpiece_clock 座鐘 時鐘|
5|1F55B|🕛️|twelve o’clock|十二點|12 12:00 clock o’clock time twelve twelve o’clock clock12 十二點 午夜 正午|
5|1F567|🕧️|twelve-thirty|十二點半|12 12:30 30 clock thirty time twelve twelve-thirty clock1230 十二點半 十二點半|
5|1F550|🕐️|one o’clock|一點|1 1:00 clock one o’clock time one o’clock clock1 一點 一點|
5|1F55C|🕜️|one-thirty|一點半|1 1:30 30 clock one thirty time one-thirty clock130 一點半 時鐘 時間|
5|1F551|🕑️|two o’clock|兩點|2 2:00 clock o’clock time two two o’clock clock2 兩點 兩點|
5|1F55D|🕝️|two-thirty|兩點半|2 2:30 30 clock thirty time two two-thirty clock230 兩點半 兩點半|
5|1F552|🕒️|three o’clock|三點|3 3:00 clock o’clock three time three o’clock clock3 三點 三點|
5|1F55E|🕞️|three-thirty|三點半|3 30 3:30 clock thirty three time three-thirty clock330 三點半 三點半|
5|1F553|🕓️|four o’clock|四點|4 4:00 clock four o’clock time four o’clock clock4 四點 四點|
5|1F55F|🕟️|four-thirty|四點半|30 4 4:30 clock four thirty time four-thirty clock430 四點半 四點半|
5|1F554|🕔️|five o’clock|五點|5 5:00 clock five o’clock time five o’clock clock5 五點 五點|
"""

private const val EMOJI_ROWS_6 = """
5|1F560|🕠️|five-thirty|五點半|30 5 5:30 clock five thirty time five-thirty clock530 五點半 五點半|
5|1F555|🕕️|six o’clock|六點|6 6:00 clock o’clock six time six o’clock clock6 六點 六點|
5|1F561|🕡️|six-thirty|六點半|30 6 6:30 clock six thirty six-thirty clock630 六點半 六點半|
5|1F556|🕖️|seven o’clock|七點|0 7 7:00 clock o’clock seven seven o’clock clock7 七點 七點|
5|1F562|🕢️|seven-thirty|七點半|30 7 7:30 clock seven thirty seven-thirty clock730 七點半 七點半|
5|1F557|🕗️|eight o’clock|八點|8 8:00 clock eight o’clock time eight o’clock clock8 八點 八點|
5|1F563|🕣️|eight-thirty|八點半|30 8 8:30 clock eight thirty time eight-thirty clock830 八點半 八點半|
5|1F558|🕘️|nine o’clock|九點|9 9:00 clock nine o’clock time nine o’clock clock9 九點 九點|
5|1F564|🕤️|nine-thirty|九點半|30 9 9:30 clock nine thirty time nine-thirty clock930 九點半 九點半|
5|1F559|🕙️|ten o’clock|十點|0 10 10:00 clock o’clock ten ten o’clock clock10 十點 十點|
5|1F565|🕥️|ten-thirty|十點半|10 10:30 30 clock ten thirty time ten-thirty clock1030 十點半 十點半|
5|1F55A|🕚️|eleven o’clock|十一點|11 11:00 clock eleven o’clock time eleven o’clock clock11 十一點 十一點|
5|1F566|🕦️|eleven-thirty|十一點半|11 11:30 30 clock eleven thirty time eleven-thirty clock1130 十一點半 十一點半|
5|1F311|🌑|new moon|新月|dark moon new space new moon new_moon 新月 月亮 朔月|
5|1F312|🌒|waxing crescent moon|眉形新月|crescent dreams moon space waxing waxing crescent moon waxing_crescent_moon 眉形新月 彎月|
5|1F313|🌓|first quarter moon|上弦月|first moon quarter space first quarter moon first_quarter_moon 上弦月 月亮|
5|1F314|🌔|waxing gibbous moon|盈凸月|gibbous moon space waxing waxing gibbous moon waxing_gibbous_moon 盈凸月 月亮|
5|1F315|🌕️|full moon|滿月|full moon space full moon full_moon 滿月 月亮 望月|
5|1F316|🌖|waning gibbous moon|虧凸月|gibbous moon space waning waning gibbous moon waning_gibbous_moon 虧凸月 漸盈月|
5|1F317|🌗|last quarter moon|下弦月|last moon quarter space last quarter moon last_quarter_moon 下弦月 月亮|
5|1F318|🌘|waning crescent moon|眉形殘月|crescent moon space waning waning crescent moon waning_crescent_moon 眉形殘月 殘月|
5|1F319|🌙|crescent moon|彎月|crescent moon ramadan space crescent moon crescent_moon 彎月 新月 月亮 月牙 残月|
5|1F31A|🌚|new moon face|月亮公公|face moon new space new moon face new_moon_with_face 月亮公公 新月臉 月亮 朔月|
5|1F31B|🌛|first quarter moon face|彎月臉朝左|face first moon quarter space first quarter moon face first_quarter_moon_with_face 彎月臉朝左 上弦月 月亮 眉月|
5|1F31C|🌜️|last quarter moon face|彎月臉朝右|dreams face last moon quarter last quarter moon face last_quarter_moon_with_face 彎月臉朝右 下弦月 月亮|
5|1F321|🌡️|thermometer|溫度計|weather thermometer 溫度計 溫度計|
5|2600|☀️|sun|太陽|bright rays space sunny weather sun 太陽 明亮 晴天 晴朗 陽光|
5|1F31D|🌝|full moon face|微笑的滿月|bright face full moon full moon face full_moon_with_face 微笑的滿月 月亮|
5|1F31E|🌞|sun with face|微笑的太陽|beach bright day face heat shine sun sunny sunshine weather sun with face sun_with_face 微笑的太陽 太陽|
5|1FA90|🪐|ringed planet|帶行星環的行星|planet ringed saturn saturnine ringed planet ringed_planet 帶行星環的行星 土星 土星環|
5|2B50|⭐️|star|星星|astronomy medium stars white star 星星 白色中型星|
5|1F31F|🌟|glowing star|閃爍的星星|glittery glow glowing night shining sparkle star win glowing star star2 閃爍的星星 星星|
5|1F320|🌠|shooting star|流星|falling night shooting space star shooting star stars 流星 星星 星空|
5|1F30C|🌌|milky way|銀河|milky space way milky way milky_way 銀河 星空|
5|2601|☁️|cloud|雲|weather cloud 雲 天氣 有雲 陰天|
5|26C5|⛅️|sun behind cloud|陰天|behind cloud cloudy sun weather sun behind cloud partly_sunny 陰天 天氣|
5|26C8|⛈️|cloud with lightning and rain|雷雨|cloud lightning rain thunder thunderstorm cloud with lightning and rain cloud_with_lightning_and_rain 雷雨 天氣 暴風雨 風暴|
5|1F324|🌤️|sun behind small cloud|晴偶有雲|behind cloud sun weather sun behind small cloud sun_behind_small_cloud 晴偶有雲 天氣|
5|1F325|🌥️|sun behind large cloud|晴時多雲|behind cloud sun weather sun behind large cloud sun_behind_large_cloud 晴時多雲 多雲到晴 天氣|
5|1F326|🌦️|sun behind rain cloud|晴時有雨|behind cloud rain sun weather sun behind rain cloud sun_behind_rain_cloud 晴時有雨 天氣|
5|1F327|🌧️|cloud with rain|雨天|cloud rain weather cloud with rain cloud_with_rain 雨天 天氣|
5|1F328|🌨️|cloud with snow|下雪|cloud cold snow weather cloud with snow cloud_with_snow 下雪 天氣 有雲有雪|
5|1F329|🌩️|cloud with lightning|閃電|cloud lightning weather cloud with lightning cloud_with_lightning 閃電 天氣 有雲有雷電|
5|1F32A|🌪️|tornado|龍捲風|cloud weather whirlwind tornado 龍捲風 天氣 旋風|
5|1F32B|🌫️|fog|有霧|cloud weather fog 有霧 天氣 雲 霧|
5|1F32C|🌬️|wind face|刮風|blow cloud face wind wind face wind_face 刮風 吹風 天氣|
5|1F300|🌀|cyclone|颱風|dizzy hurricane twister typhoon weather cyclone 颱風 天氣 暈 氣旋|
5|1F308|🌈|rainbow|彩虹|gay genderqueer glbt glbtq lesbian lgbt lgbtq lgbtqia nature pride queer rain trans transgender weather rainbow 彩虹 同性 跨性別 雙性|
5|1F302|🌂|closed umbrella|收合的傘|closed clothing rain umbrella closed umbrella closed_umbrella 收合的傘 傘 雨傘|
5|2602|☂️|umbrella|雨傘|clothing rain umbrella open_umbrella 雨傘 下雨 傘|
5|2614|☔️|umbrella with rain drops|雨中的傘|clothing drop drops rain umbrella weather umbrella with rain drops 雨中的傘 下雨 傘|
5|26F1|⛱️|umbrella on ground|遮陽傘|ground rain sun umbrella umbrella on ground parasol_on_ground 遮陽傘 傘|
5|26A1|⚡️|high voltage|高壓電|danger electric electricity high lightning nature thunder thunderbolt voltage zap high voltage 高壓電 閃電 雷電 電|
5|2744|❄️|snowflake|雪花|cold snow weather snowflake 雪花 下雪|
5|2603|☃️|snowman|雪中的雪人|cold man snow snowman snowman_with_snow 雪中的雪人 雪人|
5|26C4|⛄️|snowman without snow|雪人|cold man snow snowman snowman without snow 雪人 沒雪的雪人|
5|2604|☄️|comet|慧星|space comet 慧星 慧星|
5|1F525|🔥|fire|火|af burn flame hot lit litaf tool fire 火 火焰 火苗|
5|1F4A7|💧|droplet|水滴|cold comic drop nature sad sweat tear water weather droplet 水滴 汗 淚 眼淚|
5|1F30A|🌊|water wave|波浪|nature ocean surf surfer surfing water wave water wave 波浪 海浪 神奈川 衝浪|
6|1F383|🎃|jack-o-lantern|南瓜燈|celebration halloween jack lantern pumpkin jack-o-lantern jack_o_lantern 南瓜燈 南瓜 萬聖節|
6|1F384|🎄|Christmas tree|聖誕樹|celebration christmas tree christmas tree christmas_tree 聖誕樹 聖誕節|
6|1F386|🎆|fireworks|爆竹|boom celebration entertainment yolo fireworks 爆竹 慶典 焰火 煙花|
6|1F387|🎇|sparkler|煙花|boom celebration fireworks sparkle sparkler 煙花 火紅 焰火|
6|1F9E8|🧨|firecracker|鞭炮|dynamite explosive fire fireworks light pop popping spark firecracker 鞭炮 火花 火藥 炸藥 煙火 爆炸 爆竹 爆裂物|
6|2728|✨️|sparkles|閃爍|* magic sparkle star sparkles 閃爍 閃亮 閃耀 魔術|
6|1F388|🎈|balloon|氣球|birthday celebrate celebration balloon 氣球 慶祝 生日|
6|1F389|🎉|party popper|拉炮|awesome birthday celebrate celebration excited hooray party popper tada woohoo party popper 拉炮 太棒了 慶祝|
6|1F38A|🎊|confetti ball|五彩紙屑|ball celebrate celebration confetti party woohoo confetti ball confetti_ball 五彩紙屑 彩球 慶祝|
6|1F38B|🎋|tanabata tree|七夕|banner celebration japanese tanabata tree tanabata tree tanabata_tree 七夕 七夕樹 樹|
6|1F38D|🎍|pine decoration|盆栽|bamboo celebration decoration japanese pine plant pine decoration 盆栽 開運竹|
6|1F38E|🎎|Japanese dolls|女兒節|celebration doll dolls festival japanese japanese dolls 女兒節 日本娃娃 雛祭|
6|1F38F|🎏|carp streamer|鯉魚旗|carp celebration streamer carp streamer flags 鯉魚旗 鯉魚旗|
6|1F390|🎐|wind chime|風鈴|bell celebration chime wind wind chime wind_chime 風鈴 風鈴|
6|1F391|🎑|moon viewing ceremony|賞月|celebration ceremony moon viewing moon viewing ceremony rice_scene 賞月 賞月|
6|1F9E7|🧧|red envelope|紅包|envelope gift good hóngbāo lai luck money red see red envelope red_envelope 紅包 利事 吉利 壓歲錢 好兆頭 好運 禮物 禮金 紅包袋 錢|
6|1F380|🎀|ribbon|蝴蝶結|celebration ribbon 蝴蝶結 彩帶 絲帶|
6|1F381|🎁|wrapped gift|禮物|birthday bow box celebration christmas gift present surprise wrapped wrapped gift 禮物 慶祝 生日禮物 禮盒|
6|1F397|🎗️|reminder ribbon|黃絲帶|celebration reminder ribbon reminder ribbon reminder_ribbon 黃絲帶 絲帶|
6|1F39F|🎟️|admission tickets|入場券|admission ticket tickets admission tickets 入場券 票券|
6|1F3AB|🎫|ticket|門票|admission stub ticket 門票 入場券 票券 票根|
6|1F396|🎖️|military medal|軍事獎章|award celebration medal military military medal medal_military 軍事獎章 勳章|
6|1F3C6|🏆️|trophy|獎盃|champion champs prize slay sport victory win winning trophy 獎盃 冠軍 勝利 獎座 獲勝 競賽 贏了|
6|1F3C5|🏅|sports medal|獎牌|award gold medal sports winner sports medal medal_sports 獎牌 獲勝 金牌|
6|1F947|🥇|1st place medal|金牌|1st first gold medal place 1st place medal 1st_place_medal 金牌 冠軍 第一名|
6|1F948|🥈|2nd place medal|銀牌|2nd medal place second silver 2nd place medal 2nd_place_medal 銀牌 亞軍 第二名|
6|1F949|🥉|3rd place medal|銅牌|3rd bronze medal place third 3rd place medal 3rd_place_medal 銅牌 季軍 第三名|
6|26BD|⚽️|soccer ball|足球|ball football futbol soccer sport soccer ball 足球 球 運動|
6|26BE|⚾️|baseball|棒球|ball sport baseball 棒球 打球 球 運動|
6|1F94E|🥎|softball|壘球|ball glove sports underarm softball 壘球 低手 手套 球 運動|
6|1F3C0|🏀|basketball|籃球|ball hoop sport basketball 籃球 球|
6|1F3D0|🏐|volleyball|排球|ball game volleyball 排球 球 球賽|
6|1F3C8|🏈|american football|美式足球|american ball bowl football sport super american football 美式足球 球 超級盃|
6|1F3C9|🏉|rugby football|橄欖球|ball football rugby sport rugby football rugby_football 橄欖球 球 美式足球|
6|1F3BE|🎾|tennis|網球|ball racquet sport tennis 網球 球 球拍 運動|
6|1F94F|🥏|flying disc|飛盤|disc flying ultimate flying disc flying_disc 飛盤 極限|
6|1F3B3|🎳|bowling|保齡球|ball game sport strike bowling 保齡球 全倒 球|
6|1F3CF|🏏|cricket game|板球|ball bat cricket game cricket game cricket_game 板球 球|
6|1F3D1|🏑|field hockey|曲棍球|ball field game hockey stick field hockey field_hockey 曲棍球 曲棍球桿 球|
6|1F3D2|🏒|ice hockey|冰上曲棍球|game hockey ice puck stick ice hockey ice_hockey 冰上曲棍球 球|
6|1F94D|🥍|lacrosse|袋棍球|ball goal sports stick lacrosse 袋棍球 得分 球 球桿 球棍 運動 長曲棍球|
6|1F3D3|🏓|ping pong|桌球|ball bat game paddle ping pingpong pong table tennis ping pong ping_pong 桌球 乒乒 乒乓 乒乓球 球 球拍|
6|1F3F8|🏸|badminton|羽毛球|birdie game racquet shuttlecock badminton 羽毛球 球 羽球|
6|1F94A|🥊|boxing glove|拳擊手套|boxing glove boxing glove boxing_glove 拳擊手套 手套 拳擊 競技 運動|
6|1F94B|🥋|martial arts uniform|道服|arts judo karate martial taekwondo uniform martial arts uniform martial_arts_uniform 道服 柔道 武術 空手道 競技 跆拳道 運動|
6|1F945|🥅|goal net|球門|goal net goal net goal_net 球門 球網 運動|
6|26F3|⛳️|flag in hole|高爾夫|flag golf hole sport flag in hole 高爾夫 旗桿|
6|26F8|⛸️|ice skate|溜冰鞋|ice skate skating ice skate ice_skate 溜冰鞋 溜冰 滑冰|
6|1F3A3|🎣|fishing pole|釣魚|entertainment fish fishing pole sport fishing pole fishing_pole_and_fish 釣魚 釣竿 釣魚竿|
6|1F93F|🤿|diving mask|潛水面罩|diving mask scuba snorkeling diving mask diving_mask 潛水面罩 水肺 水肺潛水 浮潛 潛水|
6|1F3BD|🎽|running shirt|運動服|athletics running sash shirt running shirt running_shirt_with_sash 運動服 運動 運動衫 飾帶|
6|1F3BF|🎿|skis|滑雪|ski snow sport skis 滑雪 滑雪|
6|1F6F7|🛷|sled|雪橇|luge sledge sleigh snow toboggan sled 雪橇 平底雪橇 滑雪橇|
6|1F94C|🥌|curling stone|冰石壺|curling game rock stone curling stone curling_stone 冰石壺 冰上溜石 冰壺 石塊 石頭 遊戲|
6|1F3AF|🎯|bullseye|命中|bull dart direct entertainment game hit target bullseye 命中 正中紅心|
6|1FA80|🪀|yo-yo|溜溜球|fluctuate toy yo-yo yo_yo 溜溜球 搖擺不定 滾動 玩具|
6|1FA81|🪁|kite|風箏|fly soar kite 風箏 上升 放風箏 飛|
6|1F52B|🔫|water pistol|水槍|gun handgun pistol revolver tool water weapon water pistol 水槍 手槍 槍 武器|
6|1F3B1|🎱|pool 8 ball|撞球|8 8ball ball billiard eight game pool pool 8 ball 撞球 8 八 八號球 球|
6|1F52E|🔮|crystal ball|水晶球|ball crystal fairy fairytale fantasy fortune future magic tale tool crystal ball crystal_ball 水晶球 占卜 算命|
6|1FA84|🪄|magic wand|魔杖|magic magician wand witch wizard magic wand magic_wand 魔杖 巫婆 巫師 魔法 魔術 魔術師|
6|1F3AE|🎮️|video game|電玩|controller entertainment game video video game video_game 電玩 xbox 遙控器 電動|
6|1F579|🕹️|joystick|搖桿|game video videogame joystick 搖桿 操控桿 電玩|
6|1F3B0|🎰|slot machine|吃角子老虎|casino gamble gambling game machine slot slots slot machine slot_machine 吃角子老虎 拉霸機 賭博 賭場|
6|1F3B2|🎲|game die|骰子|dice die entertainment game game die game_die 骰子 擲骰子|
6|1F9E9|🧩|puzzle piece|拼圖|clue interlocking jigsaw piece puzzle puzzle piece 拼圖 拼 片 益智遊戲 相扣 線索 謎題|
6|1F9F8|🧸|teddy bear|泰迪熊|bear plaything plush stuffed teddy toy teddy bear teddy_bear 泰迪熊 填充 填充玩具 娃娃 毛茸茸 玩具 玩具熊 玩物 長毛絨|
6|1FA85|🪅|piñata|皮納塔|candy celebrate celebration cinco de festive mayo party pinada pinata piñata 皮納塔 五月節 墨西哥 慶祝 派對 節慶 糖果|
6|1FAA9|🪩|mirror ball|鏡面球|ball dance disco glitter mirror party mirror ball mirror_ball 鏡面球 派對 球 發光 跳舞 迪斯可 鏡子|
6|1FA86|🪆|nesting dolls|俄羅斯娃娃|babooshka baboushka babushka doll dolls matryoshka nesting russia nesting dolls nesting_dolls 俄羅斯娃娃 俄羅斯 多層 娃娃|
6|2660|♠️|spade suit|黑桃|card game spade suit spade suit spades 黑桃 撲克牌 紙牌 花色|
6|2665|♥️|heart suit|紅心|card emotion game heart hearts suit heart suit 紅心 紙牌|
6|2666|♦️|diamond suit|方塊|card diamond game suit diamond suit diamonds 方塊 牌局 紙牌 鑽石|
6|2663|♣️|club suit|梅花|card club clubs game suit club suit 梅花 紙牌|
6|265F|♟️|chess pawn|卒|chess dupe expendable pawn chess pawn chess_pawn 卒 戰略 棋子 鬥智|
6|1F0CF|🃏|joker|鬼牌|card game wildcard joker black_joker 鬼牌 外卡 小丑 皇牌|
6|1F004|🀄️|mahjong red dragon|紅中|dragon game mahjong red mahjong red dragon 紅中 麻將|
6|1F3B4|🎴|flower playing cards|花牌|card cards flower game japanese playing flower playing cards flower_playing_cards 花牌 花鬥|
6|1F3AD|🎭️|performing arts|戲劇|actor actress art arts entertainment mask performing theater theatre thespian performing arts performing_arts 戲劇 演員 面具|
6|1F5BC|🖼️|framed picture|裱框畫|art frame framed museum painting picture framed picture framed_picture 裱框畫 畫 畫框|
6|1F3A8|🎨|artist palette|調色板|art artist artsy arty colorful creative entertainment museum painter painting palette artist palette 調色板 畫畫 調色盤|
6|1F9F5|🧵|thread|線|needle sewing spool string thread 線 線卷 線軸 縫紉 裁縫 針 針線|
6|1FAA1|🪡|sewing needle|裁縫針|embroidery needle sew sewing stitches sutures tailoring thread sewing needle sewing_needle 裁縫針 刺繡 線 縫 縫紉 縫衣針 針 針線|
6|1F9F6|🧶|yarn|毛線球|ball crochet knit yarn 毛線球 毛線 球 編織 織 針織 鉤針 鉤針編織|
6|1FAA2|🪢|knot|繩結|cord rope tangled tie twine twist knot 繩結 交織 打結 細繩 結|
7|1F453|👓️|glasses|眼鏡|clothing eye eyeglasses eyewear glasses 眼鏡 眼鏡|
7|1F576|🕶️|sunglasses|太陽眼鏡|dark eye eyewear glasses sunglasses dark_sunglasses 太陽眼鏡 墨鏡 暗色|
7|1F97D|🥽|goggles|蛙鏡|dive eye protection scuba swimming welding goggles 蛙鏡 保護眼睛 游泳 潛水 焊工 護目鏡|
7|1F97C|🥼|lab coat|實驗袍|clothes coat doctor dr experiment jacket lab scientist white lab coat lab_coat 實驗袍 上衣 實驗 實驗服 科學家 衣服 醫生|
7|1F9BA|🦺|safety vest|救生衣|emergency safety vest safety vest safety_vest 救生衣 安全 緊急狀況 背心|
7|1F454|👔|necktie|領帶|clothing employed serious shirt tie necktie 領帶 工作 正式|
7|1F455|👕|t-shirt|T恤|blue casual clothes clothing collar dressed shirt shopping tshirt weekend t-shirt t恤 t卹 t恤 上衣 衣服 襯衫|
7|1F456|👖|jeans|牛仔褲|blue casual clothes clothing denim dressed pants shopping trousers weekend jeans 牛仔褲 丹寧褲 休閒 褲子 週末打扮|
7|1F9E3|🧣|scarf|圍巾|bundle cold neck up scarf 圍巾 包得緊緊 圍脖 好冷 脖子 領巾 頸子 頸巾|
7|1F9E4|🧤|gloves|手套|hand gloves 手套 手|
7|1F9E5|🧥|coat|外套|brr bundle cold jacket up coat 外套 冷 包得緊緊 夾克 好冷|
7|1F9E6|🧦|socks|襪子|stocking socks 襪子 絲襪|
7|1F457|👗|dress|洋裝|clothes clothing dressed fancy shopping dress 洋裝 血拼 裙子|
7|1F458|👘|kimono|和服|clothing comfortable kimono 和服 日本 舒服|
7|1F97B|🥻|sari|莎麗服|clothing dress sari 莎麗服 洋裝 衣服|
7|1FA71|🩱|one-piece swimsuit|一件式泳裝|bathing one-piece suit swimsuit one-piece swimsuit one_piece_swimsuit 一件式泳裝 泳衣 泳裝|
7|1FA72|🩲|briefs|泳褲|bathing one-piece suit swimsuit underwear briefs swim_brief 泳褲 一件式 內衣 泳裝 短褲|
7|1FA73|🩳|shorts|短泳褲|bathing pants suit swimsuit underwear shorts 短泳褲 內衣 泳裝 泳褲 短褲 褲裝|
7|1F459|👙|bikini|比基尼|bathing beach clothing pool suit swim bikini 比基尼 三點式 海灘 游泳 游泳池|
7|1F45A|👚|woman’s clothes|女裝|blouse clothes clothing collar dress dressed lady shirt shopping woman woman’s woman’s clothes womans_clothes 女裝 女襯衫 血拼 衣服|
7|1FAAD|🪭|folding hand fan|摺扇|clack clap cool cooling dance fan flirt flutter folding hand hot shy folding hand fan folding_hand_fan 摺扇 害羞 扇子 扇子，降溫，調情，跳舞，害羞，拍手，搧，熱 擺動 涼快 熱 舞蹈|
7|1F45B|👛|purse|錢包|clothes clothing coin dress fancy handbag shopping purse 錢包 手提包 荷包|
7|1F45C|👜|handbag|手提包|bag clothes clothing dress lady purse shopping handbag 手提包 包包 血拼|
7|1F45D|👝|clutch bag|手拿包|bag clothes clothing clutch dress handbag pouch purse clutch bag 手拿包 包包 手提包|
7|1F6CD|🛍️|shopping bags|紙袋|bag bags hotel shopping shopping bags 紙袋 購物袋|
7|1F392|🎒|backpack|書包|backpacking bag bookbag education rucksack satchel school backpack school_satchel 書包 肩揹書包 背包|
7|1FA74|🩴|thong sandal|人字拖|beach flip flop sandal sandals shoe thong thongs zōri thong sandal thong_sandal 人字拖 拖鞋 沙灘涼鞋 海灘鞋 涼鞋 草鞋 鞋|
7|1F45E|👞|man’s shoe|皮鞋|brown clothes clothing feet foot kick man man’s shoe shoes shopping man’s shoe mans_shoe 皮鞋 咖啡色 血拼 鞋|
7|1F45F|👟|running shoe|運動鞋|athletic clothes clothing fast kick running shoe shoes shopping sneaker tennis running shoe athletic_shoe 運動鞋 球鞋 跑鞋 踢|
7|1F97E|🥾|hiking boot|登山靴|backpacking boot brown camping hiking outdoors shoe hiking boot hiking_boot 登山靴 健行 戶外 背包 露營 靴子 鞋子|
7|1F97F|🥿|flat shoe|平底鞋|ballet comfy flat flats shoe slip-on slipper flat shoe flat_shoe 平底鞋 懶人鞋 拖鞋 淺口便鞋 無帶便鞋 舒服 芭蕾舞鞋|
7|1F460|👠|high-heeled shoe|高跟鞋|clothes clothing dress fashion heel heels high-heeled shoe shoes shopping stiletto woman high-heeled shoe high_heel 高跟鞋 女鞋 細高跟鞋 血拼|
7|1F461|👡|woman’s sandal|涼鞋|clothing sandal shoe woman woman’s woman’s sandal 涼鞋 拖鞋|
7|1FA70|🩰|ballet shoes|芭蕾舞鞋|ballet dance shoes ballet shoes ballet_shoes 芭蕾舞鞋 舞 芭蕾|
7|1F462|👢|woman’s boot|靴子|boot clothes clothing dress shoe shoes shopping woman woman’s woman’s boot 靴子 女靴 長靴|
7|1FAAE|🪮|hair pick|梳子|afro comb groom hair pick hair pick hair_pick 梳子 梳子，梳，頭髮，打扮，蓬髮 梳理 非洲式髮型 頭髮|
7|1F451|👑|crown|皇冠|clothing family king medieval queen royal royalty win crown 皇冠 后冠 皇家 皇族|
7|1F452|👒|woman’s hat|帽子|clothes clothing garden hat hats party woman woman’s woman’s hat womans_hat 帽子 庭園派對 淑女帽|
7|1F3A9|🎩|top hat|禮帽|clothes clothing fancy formal hat magic top tophat top hat 禮帽 紳士帽 變魔術 魔術|
7|1F393|🎓️|graduation cap|畢業帽|cap celebration clothing education graduation hat scholar graduation cap mortar_board 畢業帽 學士帽 畢業|
7|1F9E2|🧢|billed cap|鴨舌帽|baseball bent billed cap dad hat billed cap billed_cap 鴨舌帽 便帽 大嘴帽 彎簷帽 棒球帽 爸爸帽|
7|1FA96|🪖|military helmet|頭盔|army helmet military soldier war warrior military helmet military_helmet 頭盔 士兵 安全帽 戰爭 戰鬥 軍人 軍隊 陸軍|
7|26D1|⛑️|rescue worker’s helmet|工程安全帽|aid cross face hat helmet rescue worker’s rescue worker’s helmet rescue_worker_helmet 工程安全帽 安全帽|
7|1F4FF|📿|prayer beads|項鍊|beads clothing necklace prayer religion prayer beads prayer_beads 項鍊 祈禱 首飾|
7|1F484|💄|lipstick|口紅|cosmetics date makeup lipstick 口紅 化妝 化妝品 打扮|
7|1F48D|💍|ring|戒指|diamond engaged engagement married romance shiny sparkling wedding ring 戒指 結婚 訂婚 鑽戒 鑽石 閃亮|
7|1F48E|💎|gem stone|鑽石|diamond engagement gem jewel money romance stone wedding gem stone 鑽石 婚禮 寶石 訂婚|
7|1F507|🔇|muted speaker|關掉喇叭|mute muted quiet silent sound speaker muted speaker 關掉喇叭 關掉聲音 静音|
7|1F508|🔈️|speaker low volume|喇叭|low soft sound speaker volume speaker low volume 喇叭 低音量|
7|1F509|🔉|speaker medium volume|低音量|medium sound speaker volume speaker medium volume 低音量 中音 喇叭|
7|1F50A|🔊|speaker high volume|高音量|high loud music sound speaker volume speaker high volume loud_sound 高音量 喇叭 揚聲器 聲音|
7|1F4E2|📢|loudspeaker|大聲公|address communication loud public sound loudspeaker 大聲公 喇叭|
7|1F4E3|📣|megaphone|擴音器|cheering sound megaphone mega 擴音器 喇叭|
7|1F4EF|📯|postal horn|郵件通知|horn post postal postal horn postal_horn 郵件通知 號角 通知|
7|1F514|🔔|bell|鈴鐺|break church sound bell 鈴鐺 下課 鐘聲|
7|1F515|🔕|bell with slash|靜音|bell forbidden mute no not prohibited quiet silent slash sound bell with slash no_bell 靜音 無聲|
7|1F3BC|🎼|musical score|樂譜|music musical note score musical score musical_score 樂譜 音樂 音符|
7|1F3B5|🎵|musical note|音符|music musical note sound musical note musical_note 音符 音樂|
7|1F3B6|🎶|musical notes|樂符|music musical note notes sound musical notes 樂符 音樂|
7|1F399|🎙️|studio microphone|錄音室麥克風|mic microphone music studio studio microphone studio_microphone 錄音室麥克風 錄音室 麥克風|
7|1F39A|🎚️|level slider|調整桿|level music slider level slider level_slider 調整桿 滑桿|
7|1F39B|🎛️|control knobs|控制旋鈕|control knobs music control knobs control_knobs 控制旋鈕 旋鈕|
7|1F3A4|🎤|microphone|麥克風|karaoke mic music sing sound microphone 麥克風 k歌 卡拉ok 唱歌|
7|1F3A7|🎧️|headphone|耳機|earbud sound headphone headphones 耳機 聲音 音樂|
7|1F4FB|📻️|radio|收音機|entertainment tbt video radio 收音機 收音機|
7|1F3B7|🎷|saxophone|薩克斯風|instrument music sax saxophone 薩克斯風 樂器|
7|1F3BA|🎺|trumpet|小號|instrument music trumpet 小號 樂器|
7|1FA8A|🪊|trombone|長號|brass instrument jazz music sad slide trombone 長號 傷心 悲傷 樂器 滑管 爵士樂 銅管樂器 難過 音樂|
7|1FA97|🪗|accordion|手風琴|box concertina instrument music squeeze squeezebox accordion 手風琴 六角手風琴 樂器 音樂|
7|1F3B8|🎸|guitar|吉他|instrument music strat guitar 吉他 樂器 電吉他|
7|1F3B9|🎹|musical keyboard|鋼琴|instrument keyboard music musical piano musical keyboard musical_keyboard 鋼琴 樂器 鍵盤樂器 電子琴|
7|1F3BB|🎻|violin|小提琴|instrument music violin 小提琴 樂器|
7|1FA95|🪕|banjo|斑鳩琴|music stringed banjo 斑鳩琴 弦樂器 樂器 音樂|
7|1F941|🥁|drum|鼓|drumsticks music drum 鼓 打擊樂 音樂 鼓棒 鼓槌|
7|1FA98|🪘|long drum|長鼓|beat conga drum instrument long rhythm long drum long_drum 長鼓 小鼓 康加舞 康加鼓 打擊 樂器 節奏 鼓|
7|1FA87|🪇|maracas|沙鈴|cha dance instrument music party percussion rattle shake shaker maracas 沙鈴 搖動 敲擊 樂器 沙球，跳舞，趴踢，宴會，搖，恰恰，音樂，樂器，敲擊樂器，打擊樂 音樂 響聲|
7|1FA88|🪈|flute|長笛|band fife flautist instrument marching music orchestra piccolo pipe recorder woodwind flute 長笛 木管樂器 橫笛 直笛 管樂器 豎笛 長笛，木管樂，長笛手，樂隊，樂儀隊，短笛，管弦樂團，管樂器，豎笛，音樂，橫笛，樂器 音樂|
7|1FA89|🪉|harp|豎琴|cupid instrument love music orchestra harp 豎琴 丘比特 愛 樂器 樂團 音樂|
7|1F4F1|📱|mobile phone|手機|cell communication mobile phone telephone mobile phone iphone 手機 行動電話 電話|
7|1F4F2|📲|mobile phone with arrow|接電話|arrow build call cell communication mobile phone receive telephone mobile phone with arrow calling 接電話 手機 電話|
7|260E|☎️|telephone|電話|phone telephone 電話 市話|
7|1F4DE|📞|telephone receiver|聽筒|communication phone receiver telephone voip telephone receiver telephone_receiver 聽筒 電話|
7|1F4DF|📟️|pager|呼叫器|communication pager 呼叫器 bb call|
7|1F4E0|📠|fax machine|傳真機|communication fax machine fax machine 傳真機 fax|
7|1F50B|🔋|battery|電池|battery 電池 電池|
7|1FAAB|🪫|low battery|電量不足|battery drained electronic energy low power low battery low_battery 電量不足 低電力 低電量 耗盡 電 電池|
7|1F50C|🔌|electric plug|插頭|electric electricity plug electric plug electric_plug 插頭 電力|
7|1F4BB|💻️|laptop|筆電|computer office pc personal laptop 筆電 個人電腦 筆記型電腦|
7|1F5A5|🖥️|desktop computer|電腦|computer desktop monitor desktop computer desktop_computer 電腦 桌上型電腦 桌機 顯示器|
7|1F5A8|🖨️|printer|印表機|computer printer 印表機 列表機|
7|2328|⌨️|keyboard|鍵盤|computer keyboard 鍵盤 電腦|
7|1F5B1|🖱️|computer mouse|滑鼠|computer mouse computer mouse computer_mouse 滑鼠 電腦 電腦滑鼠|
7|1F5B2|🖲️|trackball|軌跡球|computer trackball 軌跡球 軌跡球|
7|1F4BD|💽|computer disk|迷你光碟|computer disk minidisk optical computer disk minidisc 迷你光碟 光碟|
7|1F4BE|💾|floppy disk|磁碟片|computer disk floppy floppy disk floppy_disk 磁碟片 磁碟片|
7|1F4BF|💿️|optical disk|光碟|blu-ray cd computer disk dvd optical optical disk 光碟 cd 硬碟 藍光|
7|1F4C0|📀|dvd|DVD|blu-ray cd computer disk optical dvd dvd dvd 光碟 藍光|
7|1F9EE|🧮|abacus|算盤|calculation calculator abacus 算盤 演算 計算 計算機|
7|1F3A5|🎥|movie camera|電影攝影機|bollywood camera cinema film hollywood movie record movie camera movie_camera 電影攝影機 寶萊塢 攝影機 電影|
7|1F39E|🎞️|film frames|電影膠卷|cinema film frames movie film frames film_strip 電影膠卷 影片 膠卷|
7|1F4FD|📽️|film projector|電影放映機|cinema film movie projector video film projector film_projector 電影放映機 影片 放映機 電影|
7|1F3AC|🎬️|clapper board|場記板|action board clapper movie clapper board 場記板 開麥拉|
7|1F4FA|📺️|television|電視|tv video television 電視 電視|
7|1F4F7|📷️|camera|相機|photo selfie snap tbt trip video camera 相機 相機|
7|1F4F8|📸|camera with flash|開閃光燈|camera flash video camera with flash camera_flash 開閃光燈 帶閃光燈的相機 拍照|
7|1F4F9|📹️|video camera|攝影機|camcorder camera tbt video video camera video_camera 攝影機 攝錄影機 錄影|
7|1F4FC|📼|videocassette|錄影帶|old school tape vcr vhs video videocassette 錄影帶 vhs 卡帶|
7|1F50D|🔍️|magnifying glass tilted left|向左的放大鏡|glass lab left left-pointing magnifying science search tilted tool magnifying glass tilted left mag 向左的放大鏡 搜尋 放大鏡|
7|1F50E|🔎|magnifying glass tilted right|放大鏡|contact glass lab magnifying right right-pointing science search tilted tool magnifying glass tilted right mag_right 放大鏡 享有的放大鏡 搜尋 放大|
7|1F56F|🕯️|candle|蠟燭|light candle 蠟燭 光亮|
7|1F4A1|💡|light bulb|燈泡|bulb comic electric idea light light bulb 燈泡 燈泡|
7|1F526|🔦|flashlight|手電筒|electric light tool torch flashlight 手電筒 手電筒|
7|1F3EE|🏮|red paper lantern|燈籠|bar lantern light paper red restaurant red paper lantern izakaya_lantern 燈籠 居酒屋 紅燈籠|
7|1FA94|🪔|diya lamp|陶碗油燈|diya lamp light oil diya lamp diya_lamp 陶碗油燈 油 油燈 燈 陶碗|
7|1F4D4|📔|notebook with decorative cover|彩色封面的筆記本|book cover decorated decorative education notebook school writing notebook with decorative cover notebook_with_decorative_cover 彩色封面的筆記本 筆記本|
7|1F4D5|📕|closed book|合起來的書本|book closed education closed book closed_book 合起來的書本 書本|
7|1F4D6|📖|open book|打開來的書本|book education fantasy knowledge library novels open reading open book open_book 打開來的書本 小說 書本 知識 讀書 閱讀|
7|1F4D7|📗|green book|綠色的書本|book education fantasy green library reading green book green_book 綠色的書本 圖書館 書本|
7|1F4D8|📘|blue book|藍色的書本|blue book education fantasy library reading blue book blue_book 藍色的書本 書本|
7|1F4D9|📙|orange book|橘色的書本|book education fantasy library orange reading orange book orange_book 橘色的書本 書本|
7|1F4DA|📚️|books|書本|book education fantasy knowledge library novels reading school study books 書本 書 書籍|
7|1F4D3|📓|notebook|筆記本|notebook 筆記本 筆記本|
7|1F4D2|📒|ledger|帳本|notebook ledger 帳本 帳簿 筆記本|
7|1F4C3|📃|page with curl|文件檔|curl document page paper page with curl page_with_curl 文件檔 捲起的頁面 文件 文書|
7|1F4DC|📜|scroll|捲軸|paper scroll 捲軸 文書 紙張|
7|1F4C4|📄|page facing up|文件|document facing page paper up page facing up page_facing_up 文件 文書 文檔|
7|1F4F0|📰|newspaper|報紙|communication news paper newspaper 報紙 新聞|
7|1F5DE|🗞️|rolled-up newspaper|捲好的報紙|news newspaper paper rolled rolled-up rolled-up newspaper newspaper_roll 捲好的報紙 報紙 捲起 新聞|
7|1F4D1|📑|bookmark tabs|頁籤|bookmark mark marker tabs bookmark tabs bookmark_tabs 頁籤 分頁標籤 標籤|
7|1F516|🔖|bookmark|書籤|mark bookmark 書籤 書籤|
7|1F3F7|🏷️|label|吊牌|tag label 吊牌 標籤|
7|1FA99|🪙|coin|硬幣|dollar euro gold metal money rich silver treasure coin 硬幣 寶藏 歐元 金 金屬 金幣 銀 錢 錢幣|
7|1F4B0|💰️|money bag|錢袋|bag bank bet billion cash cost dollar gold million money moneybag paid paying pot rich win money bag 錢袋 一桶金 錢|
7|1FA8E|🪎|treasure chest|藏寶箱|gem gold jewels loot money prize silver valuables wealth treasure chest 藏寶箱 寶石 戰利品 搶劫 珠寶 財富 貴重物品 金子 銀 錢|
7|1F4B4|💴|yen banknote|日幣|bank banknote bill currency money note yen yen banknote 日幣 貨幣 鈔票 錢|
7|1F4B5|💵|dollar banknote|美金|bank banknote bill currency dollar money note dollar banknote 美金 貨幣 鈔票 錢|
7|1F4B6|💶|euro banknote|歐元|100 bank banknote bill currency euro money note rich euro banknote 歐元 一百歐元 貨幣 鈔票 錢|
7|1F4B7|💷|pound banknote|英鎊|bank banknote bill billion cash currency money note pound pounds pound banknote 英鎊 貨幣 鈔票 錢|
7|1F4B8|💸|money with wings|錢飛了|bank banknote bill billion cash dollar fly million money note pay wings money with wings money_with_wings 錢飛了 沒錢了 錢跑了|
7|1F4B3|💳️|credit card|信用卡|bank card cash charge credit money pay credit card credit_card 信用卡 刷卡|
7|1F9FE|🧾|receipt|收據|accounting bookkeeping evidence invoice proof receipt 收據 收執聯 會計 發票 簿記 紙本 記帳 證據 證明|
7|1F4B9|💹|chart increasing with yen|貨幣升值|bank chart currency graph growth increasing market money rise trend upward yen chart increasing with yen 貨幣升值 上揚 圖表 市場走向|
7|2709|✉️|envelope|郵件|e-mail email letter envelope 郵件 信 信封|
7|1F4E7|📧|e-mail|電子郵件|email letter mail e-mail 電子郵件 email 郵件|
7|1F4E8|📨|incoming envelope|收到郵件|delivering e-mail email envelope incoming letter mail receive sent incoming envelope incoming_envelope 收到郵件 信件 信封 接收 送信 郵件|
7|1F4E9|📩|envelope with arrow|寄出郵件|arrow communication down e-mail email envelope letter mail outgoing send sent envelope with arrow envelope_with_arrow 寄出郵件 信件 信封 發送 郵件 電子郵件|
7|1F4E4|📤️|outbox tray|寄件匣|box email letter mail outbox sent tray outbox tray outbox_tray 寄件匣 發信匣|
7|1F4E5|📥️|inbox tray|收件匣|box email inbox letter mail receive tray zero inbox tray inbox_tray 收件匣 收信匣|
7|1F4E6|📦️|package|包裹|box communication delivery parcel shipping package 包裹 寄包裹 紙箱|
7|1F4EB|📫️|closed mailbox with raised flag|有待收郵件|closed communication flag mail mailbox postbox raised closed mailbox with raised flag 有待收郵件 信箱 郵箱|
7|1F4EA|📪️|closed mailbox with lowered flag|無待收郵件|closed flag lowered mail mailbox postbox closed mailbox with lowered flag mailbox_closed 無待收郵件 信箱 郵箱|
7|1F4EC|📬️|open mailbox with raised flag|有新郵件|flag mail mailbox open postbox raised open mailbox with raised flag mailbox_with_mail 有新郵件 信箱 郵箱|
7|1F4ED|📭️|open mailbox with lowered flag|沒有新郵件|flag lowered mail mailbox open postbox open mailbox with lowered flag mailbox_with_no_mail 沒有新郵件 信箱 郵箱|
7|1F4EE|📮|postbox|信箱|mail mailbox postbox 信箱 郵件 郵箱|
7|1F5F3|🗳️|ballot box with ballot|投票箱|ballot box ballot box with ballot ballot_box 投票箱 票箱|
7|270F|✏️|pencil|鉛筆|pencil pencil2 鉛筆 鉛筆|
7|2712|✒️|black nib|鋼筆頭|black nib pen black nib black_nib 鋼筆頭 筆尖 鋼筆|
7|1F58B|🖋️|fountain pen|鋼筆|fountain pen fountain pen fountain_pen 鋼筆 鋼筆|
7|1F58A|🖊️|pen|筆|ballpoint pen 筆 原子筆|
7|1F58C|🖌️|paintbrush|畫筆|painting paintbrush 畫筆 漆刷|
7|1F58D|🖍️|crayon|蠟筆|crayon 蠟筆 蠟筆|
7|1F4DD|📝|memo|備忘錄|communication media notes pencil memo 備忘錄 備註 媒體 鉛筆|
7|1F4BC|💼|briefcase|公事包|office briefcase 公事包 辦公室|
7|1F4C1|📁|file folder|資料夾|file folder file folder file_folder 資料夾 文件夾 檔案|
7|1F4C2|📂|open file folder|打開資料夾|file folder open open file folder open_file_folder 打開資料夾 打開檔案 檔案 資料夾|
7|1F5C2|🗂️|card index dividers|索引隔板|card dividers index card index dividers card_index_dividers 索引隔板 分隔文件夹 索引卡 索引板|
7|1F4C5|📅|calendar|行事曆|date calendar 行事曆 日期|
7|1F4C6|📆|tear-off calendar|日曆|calendar tear-off tear-off calendar 日曆 撕日曆 行事曆|
7|1F5D2|🗒️|spiral notepad|線圈筆記本|note notepad pad spiral spiral notepad spiral_notepad 線圈筆記本 筆記本|
7|1F5D3|🗓️|spiral calendar|線圈日曆|calendar pad spiral spiral calendar spiral_calendar 線圈日曆 日曆|
7|1F4C7|📇|card index|索引卡|card index old rolodex school card index card_index 索引卡 名片索引|
7|1F4C8|📈|chart increasing|漲|chart data graph growth increasing right trend up upward chart increasing chart_with_upwards_trend 漲 上升 上漲 圖表|
7|1F4C9|📉|chart decreasing|跌|chart data decreasing down downward graph negative trend chart decreasing chart_with_downwards_trend 跌 下跌 下降 圖表 跌勢|
7|1F4CA|📊|bar chart|圖表|bar chart data graph bar chart bar_chart 圖表 橫條圖 直方圖|
7|1F4CB|📋️|clipboard|寫字夾板|do list notes clipboard 寫字夾板 代辦事項 筆記|
7|1F4CC|📌|pushpin|圖釘|collage pin pushpin 圖釘 大頭釘|
7|1F4CD|📍|round pushpin|圓圖釘|location map pin pushpin round round pushpin round_pushpin 圓圖釘 圓頭大頭針 圖釘 大頭針|
7|1F4CE|📎|paperclip|迴紋針|paperclip 迴紋針 迴紋針|
7|1F587|🖇️|linked paperclips|相連的回紋針|link linked paperclip paperclips linked paperclips 相連的回紋針 回紋針 相連|
7|1F4CF|📏|straight ruler|直尺|angle edge math ruler straight straightedge straight ruler straight_ruler 直尺 尺|
7|1F4D0|📐|triangular ruler|三角尺|angle math rule ruler set slide triangle triangular triangular ruler triangular_ruler 三角尺 尺|
7|2702|✂️|scissors|剪刀|cut cutting paper tool scissors 剪刀 剪 剪切 工具|
7|1F5C3|🗃️|card file box|卡片目錄盒|box card file card file box card_file_box 卡片目錄盒 卡片 目錄盒|
7|1F5C4|🗄️|file cabinet|檔案櫃|cabinet file filing paper file cabinet file_cabinet 檔案櫃 歸檔|
7|1F5D1|🗑️|wastebasket|廢紙簍|can garbage trash waste wastebasket 廢紙簍 字紙簍|
7|1F512|🔒️|locked|上鎖|closed lock private locked 上鎖 私人的 鎖|
7|1F513|🔓️|unlocked|開鎖|cracked lock open unlock unlocked 開鎖 開鎖|
7|1F50F|🔏|locked with pen|鋼筆和鎖|ink lock locked nib pen privacy locked with pen lock_with_ink_pen 鋼筆和鎖 以筆上鎖 鋼筆 鎖 隱私|
7|1F510|🔐|locked with key|鑰匙和鎖|bike closed key lock locked secure locked with key closed_lock_with_key 鑰匙和鎖 腳踏車鎖 鎖 鎖起來 鑰匙|
7|1F511|🔑|key|鑰匙|keys lock major password unlock key 鑰匙 密碼 解鎖 鎖|
"""

private const val EMOJI_ROWS_7 = """
7|1F5DD|🗝️|old key|老鑰匙|clue key lock old old key old_key 老鑰匙 鑰匙|
7|1F528|🔨|hammer|鎚子|home improvement repairs tool hammer 鎚子 榔頭|
7|1FA93|🪓|axe|斧頭|ax chop hatchet split wood axe 斧頭 分割 手斧 木頭 短斧 砍|
7|26CF|⛏️|pick|十字鎬|hammer mining tool pick 十字鎬 鑿|
7|2692|⚒️|hammer and pick|鎚子和十字鎬|hammer pick tool hammer and pick hammer_and_pick 鎚子和十字鎬 十字鎬 鎚子|
7|1F6E0|🛠️|hammer and wrench|鎚子和扳手|hammer spanner tool wrench hammer and wrench hammer_and_wrench 鎚子和扳手 扳手 榔頭和扳手 鎚子|
7|1F5E1|🗡️|dagger|匕首|knife weapon dagger 匕首 刀|
7|2694|⚔️|crossed swords|劍|crossed swords weapon crossed swords crossed_swords 劍 劍|
7|1F4A3|💣️|bomb|炸彈|boom comic dangerous explosion hot bomb 炸彈 危險 地雷 爆炸 雷區|
7|1FA83|🪃|boomerang|迴力鏢|rebound repercussion weapon boomerang 迴力鏢 原住民的 回飛棒 武器 澳洲 迴旋 迴旋鏢|
7|1F3F9|🏹|bow and arrow|弓箭|archer archery arrow bow sagittarius tool weapon zodiac bow and arrow bow_and_arrow 弓箭 射手 射手座 射箭|
7|1F6E1|🛡️|shield|盾牌|weapon shield 盾牌 盾牌|
7|1FA9A|🪚|carpentry saw|木工鋸|carpenter carpentry cut lumber saw tool trim carpentry saw carpentry_saw 木工鋸 切 工具 木工 木材 鋸子|
7|1F527|🔧|wrench|扳手|home improvement spanner tool wrench 扳手 扳手|
7|1FA9B|🪛|screwdriver|螺絲起子|flathead handy screw tool screwdriver 螺絲起子 一字起子 工具 羅賴把 螺絲|
7|1F529|🔩|nut and bolt|螺絲|bolt home improvement nut tool nut and bolt nut_and_bolt 螺絲 螺栓 螺絲與螺帽|
7|2699|⚙️|gear|齒輪|cog cogwheel tool gear 齒輪 齒輪|
7|1F5DC|🗜️|clamp|壓縮機|compress tool vice clamp 壓縮機 壓縮 夾具 鉗子|
7|2696|⚖️|balance scale|天平|balance justice libra scale scales tool weight zodiac balance scale balance_scale 天平 司法 天枰座|
7|1F9AF|🦯|white cane|導盲手杖|accessibility blind cane probing white white cane probing_cane 導盲手杖 盲人 行動不便|
7|1F517|🔗|link|連結|links link 連結 連接 鏈結|
7|26D3-FE0F-200D-1F4A5|⛓️‍💥|broken chain|斷掉的鏈條|break breaking broken chain cuffs freedom broken chain 斷掉的鏈條 斷掉、斷開、鏈條、手銬、自由|
7|26D3|⛓️|chains|鍊條|chain chains 鍊條 鍊子|
7|1FA9D|🪝|hook|鉤子|catch crook curve ensnare point selling hook 鉤子 勾 彎曲 鉤 鉤形|
7|1F9F0|🧰|toolbox|工具箱|box chest mechanic red tool toolbox 工具箱 器具 工具 機械工 箱子 紅盒子|
7|1F9F2|🧲|magnet|磁鐵|attraction horseshoe magnetic negative positive shape u magnet 磁鐵 u 型 五金 吸引 正負 磁力 磁吸 馬蹄|
7|1FA9C|🪜|ladder|梯子|climb rung step ladder 梯子 梯凳 橫木 爬 踩 階梯|
7|1FA8F|🪏|shovel|鏟子|bury dig garden hole plant scoop snow spade shovel 鏟子 挖 洞 鏟|
7|2697|⚗️|alembic|蒸餾器|chemistry tool alembic 蒸餾器 化學 蒸餾|
7|1F9EA|🧪|test tube|試管|chemist chemistry experiment lab science test tube test tube test_tube 試管 化學 化學家 實驗 實驗室 科學|
7|1F9EB|🧫|petri dish|培養皿|bacteria biologist biology culture dish lab petri petri dish petri_dish 培養皿 培養 基因 實驗 實驗室 生物 生物學家 細菌|
7|1F9EC|🧬|dna|DNA|biologist evolution gene genetics life dna dna dna 基因 演化 生命 生物 生物學家|
7|1F52C|🔬|microscope|顯微鏡|experiment lab science tool microscope 顯微鏡 實驗 實驗室|
7|1F52D|🔭|telescope|望遠鏡|contact extraterrestrial science tool telescope 望遠鏡 觀測|
7|1F4E1|📡|satellite antenna|衛星天線|aliens antenna contact dish satellite science satellite antenna 衛星天線 外星人 天線|
7|1F489|💉|syringe|針筒|doctor flu medicine needle shot sick tool vaccination syringe 針筒 注射器|
7|1FA78|🩸|drop of blood|血滴|bleed blood donation drop injury medicine menstruation drop of blood drop_of_blood 血滴 受傷 捐血 流血 生理期 藥|
7|1F48A|💊|pill|藥丸|doctor drugs medicated medicine pills sick vitamin pill 藥丸 生病 維他命 藥|
7|1FA79|🩹|adhesive bandage|OK 繃|adhesive bandage adhesive bandage adhesive_bandage ok 繃 ok 繃 繃帶|
7|1FA7C|🩼|crutch|拐杖|aid cane disability help hurt injured mobility stick crutch 拐杖 助行 受傷 手杖 行動不便 輔助|
7|1FA7A|🩺|stethoscope|聽診器|doctor heart medicine stethoscope 聽診器 心跳 藥 醫生|
7|1FA7B|🩻|x-ray|X 光|bones doctor medical skeleton skull xray x-ray x_ray x 光 x 光 醫學 醫生 骨頭 骨骼 骷髏|
7|1F6AA|🚪|door|門|back closet front door 門 前門 後門 櫃子|
7|1F6D7|🛗|elevator|電梯|accessibility hoist lift elevator 電梯 上升 方便 貨梯|
7|1FA9E|🪞|mirror|鏡子|makeup reflection reflector speculum mirror 鏡子 化妝 化妝鏡 反射 反射鏡 反映|
7|1FA9F|🪟|window|窗戶|air frame fresh opening transparent view window 窗戶 框 窗 觀景 透明|
7|1F6CF|🛏️|bed|床|hotel sleep bed 床 睡覺|
7|1F6CB|🛋️|couch and lamp|沙發和立燈|couch hotel lamp couch and lamp couch_and_lamp 沙發和立燈 沙發 立燈|
7|1FA91|🪑|chair|椅子|seat sit chair 椅子 坐 座椅|
7|1F6BD|🚽|toilet|馬桶|bathroom toilet 馬桶 廁所|
7|1FAA0|🪠|plunger|吸把|cup force plumber poop suction toilet plunger 吸把 大便 廁所 通水器 通馬桶|
7|1F6BF|🚿|shower|淋浴|water shower 淋浴 蓮蓬頭|
7|1F6C1|🛁|bathtub|澡盆|bath bathtub 澡盆 浴盆 浴缸|
7|1FAA4|🪤|mouse trap|捕鼠器|bait cheese lure mouse mousetrap snare trap mouse trap mouse_trap 捕鼠器 捕鼠 捕鼠夾 誘捕 起司 陷阱 餌|
7|1FA92|🪒|razor|剃刀|sharp shave razor 剃刀 剃 鋒利|
7|1F9F4|🧴|lotion bottle|乳液瓶|bottle lotion moisturizer shampoo sunscreen lotion bottle lotion_bottle 乳液瓶 乳液 保濕 保濕乳液 洗髮精 防曬 防曬乳|
7|1F9F7|🧷|safety pin|安全別針|diaper pin punk rock safety safety pin safety_pin 安全別針 尿布 龐克搖滾|
7|1F9F9|🧹|broom|掃帚|cleaning sweeping witch broom 掃帚 巫婆 掃 掃地 掃把 清潔 清理|
7|1F9FA|🧺|basket|籃子|farming laundry picnic basket 籃子 洗衣 種植 農作 野餐|
7|1F9FB|🧻|roll of paper|捲筒衛生紙|paper roll toilet towels roll of paper roll_of_paper 捲筒衛生紙 紙巾 紙捲 衛生紙|
7|1FAA3|🪣|bucket|水桶|cask pail vat bucket 水桶 一桶 提桶 桶 桶子|
7|1F9FC|🧼|soap|肥皂|bar bathing clean cleaning lather soapdish soap 肥皂 泡沫 洗澡 清潔 肥皂盒 肥皂盤|
7|1FAE7|🫧|bubbles|泡泡|bubble burp clean floating pearl soap underwater bubbles 泡泡 打嗝 水中 清潔 漂浮 珍珠 肥皂|
7|1FAA5|🪥|toothbrush|牙刷|bathroom brush clean dental hygiene teeth toiletry toothbrush 牙刷 乾淨 刷子 浴室 清潔 牙齒 衛浴用品|
7|1F9FD|🧽|sponge|海綿|absorbing cleaning porous soak sponge 海綿 吸收 吸水 多孔 浸泡 清潔 透氣|
7|1F9EF|🧯|fire extinguisher|滅火器|extinguish extinguisher fire quench fire extinguisher fire_extinguisher 滅火器 撲滅 滅火|
7|1F6D2|🛒|shopping cart|購物車|cart shopping trolley shopping cart shopping_cart 購物車 推車 購物|
7|1F6AC|🚬|cigarette|吸煙|smoking cigarette 吸煙 抽煙 香菸 點煙|
7|26B0|⚰️|coffin|棺木|dead death vampire coffin 棺木 棺材|
7|1FAA6|🪦|headstone|墓石|cemetery dead grave graveyard memorial rip tomb tombstone headstone 墓石 rip 墓 墓園 墓碑 墳墓 安息 死 死亡 紀念|
7|26B1|⚱️|funeral urn|骨灰罈|ashes death funeral urn funeral urn funeral_urn 骨灰罈 骨灰甕|
7|1F9FF|🧿|nazar amulet|邪眼|amulet bead blue charm evil-eye nazar talisman nazar amulet nazar_amulet 邪眼 珠子 符咒 藍眼 藍色 護身符 避邪|
7|1FAAC|🪬|hamsa|法蒂瑪之手|amulet fatima fortune guide hand mary miriam palm protect protection hamsa 法蒂瑪之手 保護 幸運 手 指引 法蒂瑪 瑪麗 米利暗 護身符 避邪物|
7|1F5FF|🗿|moai|復活節島|face moyai statue stoneface travel moai 復活節島 復活島石像|
7|1FAA7|🪧|placard|告示牌|card demonstration notice picket plaque protest sign placard 告示牌 佈告 公告 告示 標示 標語 標語牌|
7|1FAAA|🪪|identification card|識別證|card credentials document id identification license security identification card identification_card 識別證 id 執照 安全 憑證 授權 證件 證照 身分證 身分證明|
8|1F3E7|🏧|ATM sign|提款機|atm automated bank cash money sign teller atm sign 提款機 提款機|
8|1F6AE|🚮|litter in bin sign|垃圾桶|bin litter litterbin sign litter in bin sign put_litter_in_its_place 垃圾桶 垃圾桶|
8|1F6B0|🚰|potable water|飲用水|drinking potable water potable water potable_water 飲用水 可飲|
8|267F|♿️|wheelchair symbol|無障礙空間|access handicap symbol wheelchair wheelchair symbol 無障礙空間 行動不便者 身障人士 輪椅|
8|1F6B9|🚹️|men’s room|男廁|bathroom lavatory man men’s restroom room toilet wc men’s room mens 男廁 男廁|
8|1F6BA|🚺️|women’s room|女廁|bathroom lavatory restroom room toilet wc woman women’s women’s room womens 女廁 女廁|
8|1F6BB|🚻|restroom|洗手間|bathroom lavatory toilet wc restroom 洗手間 廁所|
8|1F6BC|🚼️|baby symbol|寶寶|baby changing symbol baby symbol baby_symbol 寶寶 嬰兒|
8|1F6BE|🚾|water closet|廁所|bathroom closet lavatory restroom toilet water wc water closet 廁所 盥洗室|
8|1F6C2|🛂|passport control|護照查驗|control passport passport control passport_control 護照查驗 入出境關卡 護照|
8|1F6C3|🛃|customs|海關|packing customs 海關 打包|
8|1F6C4|🛄|baggage claim|提取行李|arrived baggage bags case checked claim journey packing plane ready travel trip baggage claim baggage_claim 提取行李 行李|
8|1F6C5|🛅|left luggage|寄存行李|baggage case left locker luggage left luggage left_luggage 寄存行李 寄物 寄物櫃|
8|26A0|⚠️|warning|警告|caution warning 警告 警告|
8|1F6B8|🚸|children crossing|小心兒童|child children crossing pedestrian traffic children crossing children_crossing 小心兒童 當心兒童 行人優先|
8|26D4|⛔️|no entry|禁止進入|do entry fail forbidden no not pass prohibited traffic no entry no_entry 禁止進入 禁止通行 禁行|
8|1F6AB|🚫|prohibited|禁止通行|entry forbidden no not smoke prohibited no_entry_sign 禁止通行 禁止 禁止進入|
8|1F6B3|🚳|no bicycles|禁行自行車|bicycle bicycles bike forbidden no not prohibited no bicycles no_bicycles 禁行自行車 禁止通行|
8|1F6AD|🚭️|no smoking|禁止吸煙|forbidden no not prohibited smoke smoking no smoking no_smoking 禁止吸煙 禁煙|
8|1F6AF|🚯|no littering|禁止亂丟垃圾|forbidden litter littering no not prohibited no littering do_not_litter 禁止亂丟垃圾 請勿亂丟垃圾|
8|1F6B1|🚱|non-potable water|非飲用水|dry non-drinking non-potable prohibited water non-potable water non-potable_water 非飲用水 不得生飲|
8|1F6B7|🚷|no pedestrians|禁止行人通行|forbidden no not pedestrian pedestrians prohibited no pedestrians no_pedestrians 禁止行人通行 禁止通行|
8|1F4F5|📵|no mobile phones|禁用手機|cell forbidden mobile no not phone phones prohibited telephone no mobile phones no_mobile_phones 禁用手機 禁用手機|
8|1F51E|🔞|no one under eighteen|未成年人不宜|18 age eighteen forbidden no not one prohibited restriction underage no one under eighteen 未成年人不宜 18 禁|
8|2622|☢️|radioactive|放射性|sign radioactive 放射性 標誌 輻射|
8|2623|☣️|biohazard|危害生物|sign biohazard 危害生物 對生物有害|
8|2B06|⬆️|up arrow|向上箭頭|arrow cardinal direction north up up arrow arrow_up 向上箭頭 北方 方向|
8|2197|↗️|up-right arrow|右上箭頭|arrow direction intercardinal northeast up-right up-right arrow arrow_upper_right 右上箭頭 方向 東北|
8|27A1|➡️|right arrow|向右箭頭|arrow cardinal direction east right right arrow arrow_right 向右箭頭 方向 東|
8|2198|↘️|down-right arrow|右下箭頭|arrow direction down-right intercardinal southeast down-right arrow arrow_lower_right 右下箭頭 方向 東南|
8|2B07|⬇️|down arrow|向下箭頭|arrow cardinal direction down south down arrow arrow_down 向下箭頭 南方 方向|
8|2199|↙️|down-left arrow|左下箭頭|arrow direction down-left intercardinal southwest down-left arrow arrow_lower_left 左下箭頭 方向 西南|
8|2B05|⬅️|left arrow|向左箭頭|arrow cardinal direction left west left arrow arrow_left 向左箭頭 方向 西方|
8|2196|↖️|up-left arrow|左上箭頭|arrow direction intercardinal northwest up-left up-left arrow arrow_upper_left 左上箭頭 方向 西北|
8|2195|↕️|up-down arrow|上下箭頭|arrow up-down up-down arrow arrow_up_down 上下箭頭 上下箭頭|
8|2194|↔️|left-right arrow|左右箭頭|arrow left-right left-right arrow left_right_arrow 左右箭頭 左右箭頭|
8|21A9|↩️|right arrow curving left|向左彎的右箭頭|arrow curving left right right arrow curving left leftwards_arrow_with_hook 向左彎的右箭頭 右轉箭頭|
8|21AA|↪️|left arrow curving right|向右彎的左箭頭|arrow curving left right left arrow curving right arrow_right_hook 向右彎的左箭頭 左轉箭頭|
8|2934|⤴️|right arrow curving up|右上旋轉箭頭|arrow curving right up right arrow curving up arrow_heading_up 右上旋轉箭頭 右上旋轉箭頭|
8|2935|⤵️|right arrow curving down|右下旋轉箭頭|arrow curving down right right arrow curving down arrow_heading_down 右下旋轉箭頭 右下旋轉箭頭|
8|1F503|🔃|clockwise vertical arrows|順時針方向|arrow arrows clockwise refresh reload vertical clockwise vertical arrows arrows_clockwise 順時針方向 刷新 重新載入 順時針|
8|1F504|🔄|counterclockwise arrows button|逆時針|again anticlockwise arrow arrows button counterclockwise deja refresh rewindershins vu counterclockwise arrows button arrows_counterclockwise 逆時針 倒帶 再一次 剪頭|
8|1F519|🔙|BACK arrow|返回|arrow back back arrow 返回 返回|
8|1F51A|🔚|END arrow|結束|arrow end end arrow 結束 結束|
8|1F51B|🔛|ON! arrow|ON|arrow mark on! on! arrow on on on|
8|1F51C|🔜|SOON arrow|馬上|arrow brb omw soon soon arrow 馬上 omw on my way! 在路上|
8|1F51D|🔝|TOP arrow|置頂|arrow homie top up top arrow 置頂 箭頭向上|
8|1F6D0|🛐|place of worship|祈禱|place pray religion worship place of worship place_of_worship 祈禱 祝禱|
8|269B|⚛️|atom symbol|原子|atheist atom symbol atom symbol atom_symbol 原子 無神論者|
8|1F549|🕉️|om|唵|hindu religion om 唵 印度教 梵文|
8|2721|✡️|star of David|六芒星|david jew jewish judaism religion star star of david star_of_david 六芒星 六角星 大衛之星 猶太教|
8|2638|☸️|wheel of dharma|法輪|buddhist dharma religion wheel wheel of dharma wheel_of_dharma 法輪 法輪|
8|262F|☯️|yin yang|陰陽|difficult lives religion tao taoist total yang yin yinyang yin yang yin_yang 陰陽 陰陽|
8|271D|✝️|latin cross|拉丁十字架|christ christian cross latin religion latin cross latin_cross 拉丁十字架 十字架 基督教|
8|2626|☦️|orthodox cross|東正教十字架|christian cross orthodox religion orthodox cross orthodox_cross 東正教十字架 十字架 正教會十字|
8|262A|☪️|star and crescent|伊斯蘭教星月|crescent islam muslim ramadan religion star star and crescent star_and_crescent 伊斯蘭教星月 伊斯蘭教 星月 齋戒月|
8|262E|☮️|peace symbol|和平|healing peace peaceful symbol peace symbol peace_symbol 和平 和平|
8|1F54E|🕎|menorah|猶太燭台|candelabrum candlestick hanukkah jewish judaism religion menorah 猶太燭台 猶太燭台|
8|1F52F|🔯|dotted six-pointed star|六芒星加圓點|dotted fortune jewish judaism six-pointed star dotted six-pointed star six_pointed_star 六芒星加圓點 六角星 猶太教|
8|1FAAF|🪯|khanda|堪達|deg fateh khalsa religion sikh sikhism tegh khanda 堪達 印度直劍，印度劍，法器，錫克教，錫克，善業與佩劍得勝，信仰，宗教 宗教 錫克教|
8|2648|♈️|Aries|牡羊座|aries horoscope ram zodiac 牡羊座 星座|
8|2649|♉️|Taurus|金牛座|bull horoscope ox taurus zodiac 金牛座 星座|
8|264A|♊️|Gemini|雙子座|gemini horoscope twins zodiac 雙子座 星座|
8|264B|♋️|Cancer|巨蟹座|cancer crab horoscope zodiac 巨蟹座 星座|
8|264C|♌️|Leo|獅子座|horoscope leo lion zodiac 獅子座 星座|
8|264D|♍️|Virgo|處女座|horoscope virgo zodiac 處女座 星座|
8|264E|♎️|Libra|天秤座|balance horoscope justice libra scales zodiac 天秤座 星座|
8|264F|♏️|Scorpio|天蠍座|horoscope scorpio scorpion scorpius zodiac 天蠍座 星座|
8|2650|♐️|Sagittarius|射手座|archer horoscope sagittarius zodiac 射手座 星座|
8|2651|♑️|Capricorn|摩羯座|capricorn goat horoscope zodiac 摩羯座 星座|
8|2652|♒️|Aquarius|水瓶座|aquarius bearer horoscope water zodiac 水瓶座 星座|
8|2653|♓️|Pisces|雙魚座|fish horoscope pisces zodiac 雙魚座 星座|
8|26CE|⛎️|Ophiuchus|蛇夫座|bearer ophiuchus serpent snake zodiac 蛇夫座 星座 蛇夫宮|
8|1F500|🔀|shuffle tracks button|隨機播放|arrow button crossed shuffle tracks shuffle tracks button twisted_rightwards_arrows 隨機播放 交叉|
8|1F501|🔁|repeat button|重複播放|arrow button clockwise repeat repeat button 重複播放 重複 順時針|
8|1F502|🔂|repeat single button|重複目前單曲|arrow button clockwise once repeat single repeat single button repeat_one 重複目前單曲 一次 順時針方向|
8|25B6|▶️|play button|播放|arrow button play right triangle play button arrow_forward 播放 右 按鈕|
8|23E9|⏩️|fast-forward button|向前快轉|arrow button double fast fast-forward forward fast-forward button fast_forward 向前快轉 按鈕|
8|23ED|⏭️|next track button|下一首|arrow button next scene track triangle next track button next_track_button 下一首 下一幕 按鈕|
8|23EF|⏯️|play or pause button|播放或暫停|arrow button pause play right triangle play or pause button play_or_pause_button 播放或暫停 按鈕 暫停|
8|25C0|◀️|reverse button|倒轉|arrow button left reverse triangle reverse button arrow_backward 倒轉 左 按鈕|
8|23EA|⏪️|fast reverse button|向後快轉|arrow button double fast reverse rewind fast reverse button 向後快轉 按鈕|
8|23EE|⏮️|last track button|上一首|arrow button last previous scene track triangle last track button previous_track_button 上一首 前一幕 最後一首|
8|1F53C|🔼|upwards button|向上|arrow button red up upwards upwards button arrow_up_small 向上 按鈕 紅|
8|23EB|⏫️|fast up button|快速向上|arrow button double fast up fast up button arrow_double_up 快速向上 向上箭頭|
8|1F53D|🔽|downwards button|向下|arrow button down downwards red downwards button arrow_down_small 向下 按鈕 紅|
8|23EC|⏬️|fast down button|快速向下|arrow button double down fast fast down button arrow_double_down 快速向下 向下箭頭|
8|23F8|⏸️|pause button|暫停|bar button double pause vertical pause button pause_button 暫停 按鈕 暫停鈕|
8|23F9|⏹️|stop button|停止播放|button square stop stop button stop_button 停止播放 停止 按鈕 方塊|
8|23FA|⏺️|record button|錄製|button circle record record button record_button 錄製 圓 按鈕|
8|23CF|⏏️|eject button|退出|button eject eject button eject_button 退出 按鈕|
8|1F3A6|🎦|cinema|電影院|camera film movie cinema 電影院 戲院 攝影機 電影|
8|1F505|🔅|dim button|低亮度|brightness button dim low dim button low_brightness 低亮度 微亮|
8|1F506|🔆|bright button|高亮度|bright brightness button light bright button high_brightness 高亮度 明亮|
8|1F4F6|📶|antenna bars|訊號強弱|antenna bar bars cell communication mobile phone signal telephone antenna bars signal_strength 訊號強弱 信號 訊號格數|
8|1F6DC|🛜|wireless|無線|broadband computer connectivity hotspot internet network router smartphone wi-fi wifi wlan wireless 無線 無線網路，wifi，路由器，連線，熱點，寬頻，網路，智慧型手機，電腦 網路 網際網路 電腦|
8|1F4F3|📳|vibration mode|震動模式|cell communication mobile mode phone telephone vibration vibration mode vibration_mode 震動模式 震動|
8|1F4F4|📴|mobile phone off|手機關機|cell mobile off phone telephone mobile phone off mobile_phone_off 手機關機 關閉|
8|2640|♀️|female sign|女|female sign woman female sign female_sign 女 女|
8|2642|♂️|male sign|男|male man sign male sign male_sign 男 男|
8|26A7|⚧️|transgender symbol|變性符號|symbol transgender transgender symbol transgender_symbol 變性符號 變性|
8|2716|✖️|multiply|乘|cancel multiplication sign x × multiply heavy_multiplication_x 乘 x × 乘法 乘法號 乘號 叉 取消 打叉 符號|
8|2795|➕️|plus|加|+ plus heavy_plus_sign 加 + 加號 數學 符號|
8|2796|➖️|minus|減|- heavy math sign − minus heavy_minus_sign 減 - − 數學 減號 符號 負|
8|2797|➗️|divide|除|division heavy math sign ÷ divide heavy_division_sign 除 ÷ 數學 符號 除法 除號|
8|1F7F0|🟰|heavy equals sign|粗體等號|answer equal equality equals heavy math sign heavy equals sign heavy_equals_sign 粗體等號 數學 相等 相等於 等式 等於 答案|
8|267E|♾️|infinity|無限大|forever unbounded universal infinity 無限大 全體 永遠 無限|
8|203C|‼️|double exclamation mark|雙驚嘆號|! !! bangbang double exclamation mark punctuation double exclamation mark 雙驚嘆號 標點|
8|2049|⁉️|exclamation question mark|驚嘆號加問號|! !? ? exclamation interrobang mark punctuation question exclamation question mark 驚嘆號加問號 標點|
8|2753|❓️|red question mark|紅色問號|? mark punctuation question red red question mark 紅色問號 問號 標點|
8|2754|❔️|white question mark|白色問號|? mark outlined punctuation question white white question mark grey_question 白色問號 問號 標點|
8|2755|❕️|white exclamation mark|白色驚嘆號|! exclamation mark outlined punctuation white white exclamation mark grey_exclamation 白色驚嘆號 標點 驚嘆號|
8|2757|❗️|red exclamation mark|紅色驚嘆號|! exclamation mark punctuation red red exclamation mark heavy_exclamation_mark 紅色驚嘆號 標點 驚嘆號|
8|3030|〰️|wavy dash|波浪線|dash punctuation wavy wavy dash wavy_dash 波浪線 標點 波浪形|
8|1F4B1|💱|currency exchange|貨幣兌換|bank currency exchange money currency exchange currency_exchange 貨幣兌換 換匯|
8|1F4B2|💲|heavy dollar sign|貨幣符號|billion cash charge currency dollar heavy million money pay sign heavy dollar sign heavy_dollar_sign 貨幣符號 貨幣 錢|
8|2695|⚕️|medical symbol|醫療符號|aesculapius medical medicine staff symbol medical symbol medical_symbol 醫療符號 醫學 醫療 醫藥|
8|267B|♻️|recycling symbol|回收|recycle recycling symbol recycling symbol 回收 可回收資源|
8|269C|⚜️|fleur-de-lis|鳶尾花|knights fleur-de-lis fleur_de_lis 鳶尾花 百合花|
8|1F531|🔱|trident emblem|三叉戟|anchor emblem poseidon ship tool trident trident emblem 三叉戟 錨|
8|1F4DB|📛|name badge|名牌|badge name name badge name_badge 名牌 胸牌|
8|1F530|🔰|Japanese symbol for beginner|日本初學者符號|beginner chevron green japanese leaf symbol tool yellow japanese symbol for beginner 日本初學者符號 v 型臂章 新手 日本初學者|
8|2B55|⭕️|hollow red circle|圓圈|circle heavy hollow large o red hollow red circle 圓圈 丸 圓|
8|2705|✅️|check mark button|白色勾勾|button check checked checkmark complete completed done fixed mark tick ✓ check mark button white_check_mark 白色勾勾 勾號 完成 打勾 搞定|
8|2611|☑️|check box with check|勾選|ballot box check checked done off tick ✓ check box with check ballot_box_with_check 勾選 勾號 打勾|
8|2714|✔️|check mark|打勾|check checked checkmark done heavy mark tick ✓ check mark heavy_check_mark 打勾 勾號|
8|274C|❌️|cross mark|叉|cancel cross mark multiplication multiply x × cross mark 叉 乘|
8|274E|❎️|cross mark button|叉叉|button cross mark multiplication multiply square x × cross mark button negative_squared_cross_mark 叉叉 乘 叉|
8|27B0|➰️|curly loop|日本單環標誌|curl curly loop curly loop curly_loop 日本單環標誌 單環|
8|27BF|➿️|double curly loop|日本免費電話標誌|curl curly double loop double curly loop 日本免費電話標誌 免費電話|
8|303D|〽️|part alternation mark|歌唱|alternation mark part part alternation mark part_alternation_mark 歌唱 歌記號|
8|2733|✳️|eight-spoked asterisk|八芒星|* asterisk eight-spoked eight-spoked asterisk eight_spoked_asterisk 八芒星 星號|
8|2734|✴️|eight-pointed star|八角星|* eight-pointed star eight-pointed star eight_pointed_black_star 八角星 八角星|
8|2747|❇️|sparkle|火花|* sparkle 火花 閃亮|
8|00A9|©️|copyright|版權|c copyright 版權 版權|
8|00AE|®️|registered|註冊|r registered 註冊 註冊|
8|2122|™️|trade mark|商標|mark tm trade trademark trade mark 商標 商標|
8|1FADF|🫟|splatter|潑濺|drip holi ink liquid mess paint spill stain splatter 潑濺 侯麗節 噴濺 弄髒 顏料 飛濺|
8|0023-FE0F-20E3|#️⃣|keycap: #|按鍵：#|keycap keycap: # hash 按鍵：# 按鍵|
8|002A-FE0F-20E3|*️⃣|keycap: *|按鍵：*|keycap keycap: * asterisk 按鍵：* 按鍵|
8|0030-FE0F-20E3|0️⃣|keycap: 0|按鍵：0|0 keycap zero keycap: 0 按鍵：0 0 按鍵|
8|0031-FE0F-20E3|1️⃣|keycap: 1|按鍵：1|1 keycap one keycap: 1 按鍵：1 1 一 按鍵|
8|0032-FE0F-20E3|2️⃣|keycap: 2|按鍵：2|2 keycap two keycap: 2 按鍵：2 2 二 按鍵|
8|0033-FE0F-20E3|3️⃣|keycap: 3|按鍵：3|3 keycap three keycap: 3 按鍵：3 3 三 按鍵|
8|0034-FE0F-20E3|4️⃣|keycap: 4|按鍵：4|4 four keycap keycap: 4 按鍵：4 4 四 按鍵|
8|0035-FE0F-20E3|5️⃣|keycap: 5|按鍵：5|5 five keycap keycap: 5 按鍵：5 5 五 按鍵|
8|0036-FE0F-20E3|6️⃣|keycap: 6|按鍵：6|6 keycap six keycap: 6 按鍵：6 6 六 按鍵|
8|0037-FE0F-20E3|7️⃣|keycap: 7|按鍵：7|7 keycap seven keycap: 7 按鍵：7 7 七 按鍵|
8|0038-FE0F-20E3|8️⃣|keycap: 8|按鍵：8|8 eight keycap keycap: 8 按鍵：8 8 八 按鍵|
8|0039-FE0F-20E3|9️⃣|keycap: 9|按鍵：9|9 keycap nine keycap: 9 按鍵：9 9 九 按鍵|
8|1F51F|🔟|keycap: 10|按鍵：10|keycap keycap: 10 keycap_ten 按鍵：10 按鍵|
8|1F520|🔠|input latin uppercase|大寫字母鍵|abcd input latin letters uppercase input latin uppercase capital_abcd 大寫字母鍵 abcd 大寫 字母 輸入|
8|1F521|🔡|input latin lowercase|小寫字母鍵|abcd input latin letters lowercase input latin lowercase 小寫字母鍵 abcd 字母 小寫 輸入|
8|1F522|🔢|input numbers|數字鍵|1234 input numbers input numbers 數字鍵 123 數字 輸入|
8|1F523|🔣|input symbols|符號鍵|% & input symbols ♪ 〒 input symbols 符號鍵 輸入 輸入符號|
8|1F524|🔤|input latin letters|拉丁字母鍵|abc alphabet input latin letters input latin letters 拉丁字母鍵 abc|
8|1F170|🅰️|A button (blood type)|A型|blood button type a button (blood type) a a型 a型 血型|
8|1F18E|🆎|AB button (blood type)|AB型|ab blood button type ab button (blood type) ab型 ab型 血型|
8|1F171|🅱️|B button (blood type)|B型|b blood button type b button (blood type) b型 b型 血型|
8|1F191|🆑|CL button|CL|button cl cl button cl cl|
8|1F192|🆒|COOL button|酷|button cool cool button 酷 酷|
8|1F193|🆓|FREE button|免費|button free free button 免費 免費|
8|2139|ℹ️|information|資訊|i information information_source 資訊 詢問處|
8|1F194|🆔|ID button|身分|button id identity id button 身分 id 身份|
8|24C2|Ⓜ️|circled M|M|circle circled m circled m m m|
8|1F195|🆕|NEW button|新|button new new button 新 新|
8|1F196|🆖|NG button|NG|button ng ng button ng ng 重來|
8|1F17E|🅾️|O button (blood type)|O型|blood button o type o button (blood type) o2 o型 o型 血型|
8|1F197|🆗|OK button|好的|button ok okay ok button 好的 ok 可以 沒問題|
8|1F17F|🅿️|P button|P|button p parking p button p p 停車|
8|1F198|🆘|SOS button|救命|button help sos sos button 救命 求救|
8|1F199|🆙|UP! button|UP|button mark up up! up! button up up 向上|
8|1F19A|🆚|VS button|對戰|button versus vs vs button 對戰 vs 比|
8|1F201|🈁|Japanese “here” button|日文KOKO|button here japanese katakana japanese “here” button koko 日文koko koko 日文koko 日語 片假名 這裡|
8|1F202|🈂️|Japanese “service charge” button|日文服務區|button charge japanese katakana service japanese “service charge” button sa 日文服務區 sa 日語 服務費 片假名|
8|1F237|🈷️|Japanese “monthly amount” button|月|amount button ideograph japanese monthly japanese “monthly amount” button u6708 月 月金额|
8|1F236|🈶|Japanese “not free of charge” button|有|button charge free ideograph japanese not japanese “not free of charge” button u6709 有 需付費 非免費|
8|1F22F|🈯️|Japanese “reserved” button|指|button ideograph japanese reserved japanese “reserved” button u6307 指 預約保留|
8|1F250|🉐|Japanese “bargain” button|得|bargain button ideograph japanese japanese “bargain” button ideograph_advantage 得 俗|
8|1F239|🈹|Japanese “discount” button|割|button discount ideograph japanese japanese “discount” button u5272 割 割|
8|1F21A|🈚️|Japanese “free of charge” button|無|button charge free ideograph japanese japanese “free of charge” button u7121 無 免費|
8|1F232|🈲|Japanese “prohibited” button|禁|button ideograph japanese prohibited japanese “prohibited” button u7981 禁 禁止|
8|1F251|🉑|Japanese “acceptable” button|可|acceptable button ideograph japanese japanese “acceptable” button accept 可 ok|
8|1F238|🈸|Japanese “application” button|申|application button ideograph japanese japanese “application” button u7533 申 申請|
8|1F234|🈴|Japanese “passing grade” button|合|button grade ideograph japanese passing japanese “passing grade” button u5408 合 過 過關|
8|1F233|🈳|Japanese “vacancy” button|空的|button ideograph japanese vacancy japanese “vacancy” button u7a7a 空的 空|
8|3297|㊗️|Japanese “congratulations” button|祝|button congratulations ideograph japanese japanese “congratulations” button 祝 祝|
8|3299|㊙️|Japanese “secret” button|秘|button ideograph japanese secret japanese “secret” button 秘 秘密|
8|1F23A|🈺|Japanese “open for business” button|營|business button ideograph japanese open japanese “open for business” button u55b6 營 營業中|
8|1F235|🈵|Japanese “no vacancy” button|滿|button ideograph japanese no vacancy japanese “no vacancy” button u6e80 滿 滿|
8|1F534|🔴|red circle|大紅色圓形|circle geometric red red circle red_circle 大紅色圓形 圓形 紅丸|
8|1F7E0|🟠|orange circle|橘色圓形|circle orange orange circle orange_circle 橘色圓形 圓形 橘色|
8|1F7E1|🟡|yellow circle|黃色圓形|circle yellow yellow circle yellow_circle 黃色圓形 圓形 黃色|
8|1F7E2|🟢|green circle|綠色圓形|circle green green circle green_circle 綠色圓形 圓形 綠色|
8|1F535|🔵|blue circle|大藍色圓形|blue circle geometric blue circle large_blue_circle 大藍色圓形 圓形 藍丸|
8|1F7E3|🟣|purple circle|紫色圓形|circle purple purple circle purple_circle 紫色圓形 圓形 紫色|
8|1F7E4|🟤|brown circle|褐色圓形|brown circle brown circle brown_circle 褐色圓形 咖啡色 咖啡色方形 圓形 褐色|
8|26AB|⚫️|black circle|黑色圓形|black circle geometric black circle black_circle 黑色圓形 圓形 黑丸|
8|26AA|⚪️|white circle|白色圓形|circle geometric white white circle white_circle 白色圓形 圓形 白丸|
8|1F7E5|🟥|red square|紅色方形|card penalty red square red square red_square 紅色方形 方形 紅色|
8|1F7E7|🟧|orange square|橘色方形|orange square orange square orange_square 橘色方形 方形 橘色|
8|1F7E8|🟨|yellow square|黃色方形|card penalty square yellow yellow square yellow_square 黃色方形 方形 黃色|
8|1F7E9|🟩|green square|綠色方形|green square green square green_square 綠色方形 方形 綠色|
8|1F7E6|🟦|blue square|藍色方形|blue square blue square blue_square 藍色方形 方形 藍色|
8|1F7EA|🟪|purple square|紫色方形|purple square purple square purple_square 紫色方形 方形 紫色|
8|1F7EB|🟫|brown square|褐色方形|brown square brown square brown_square 褐色方形 咖啡色 咖啡色方形 方形 褐色|
8|2B1B|⬛️|black large square|黑色大方塊|black geometric large square black large square black_large_square 黑色大方塊 方形|
8|2B1C|⬜️|white large square|白色大方塊|geometric large square white white large square white_large_square 白色大方塊 方形|
8|25FC|◼️|black medium square|黑色中方塊|black geometric medium square black medium square black_medium_square 黑色中方塊 方形|
8|25FB|◻️|white medium square|白色中方塊|geometric medium square white white medium square white_medium_square 白色中方塊 方形 白色方塊|
8|25FE|◾️|black medium-small square|黑色中小型方塊|black geometric medium-small square black medium-small square black_medium_small_square 黑色中小型方塊 方形 黑方塊|
8|25FD|◽️|white medium-small square|白色中小型方塊|geometric medium-small square white white medium-small square white_medium_small_square 白色中小型方塊 方塊 方形 白色小方塊|
8|25AA|▪️|black small square|黑色小方塊|black geometric small square black small square black_small_square 黑色小方塊 方形 黑色方塊|
8|25AB|▫️|white small square|白色小方塊|geometric small square white white small square white_small_square 白色小方塊 方形|
8|1F536|🔶|large orange diamond|大橙色菱形|diamond geometric large orange large orange diamond large_orange_diamond 大橙色菱形 大橘色鑽石 菱形|
8|1F537|🔷|large blue diamond|大藍色菱形|blue diamond geometric large large blue diamond large_blue_diamond 大藍色菱形 大藍色鑽石 菱形|
8|1F538|🔸|small orange diamond|小橙色菱形|diamond geometric orange small small orange diamond small_orange_diamond 小橙色菱形 小橘色鑽石 菱形|
8|1F539|🔹|small blue diamond|小藍色菱形|blue diamond geometric small small blue diamond small_blue_diamond 小藍色菱形 菱形|
8|1F53A|🔺|red triangle pointed up|向上紅色三角|geometric pointed red triangle up red triangle pointed up small_red_triangle 向上紅色三角 三角形|
8|1F53B|🔻|red triangle pointed down|向下紅色三角|down geometric pointed red triangle red triangle pointed down small_red_triangle_down 向下紅色三角 三角形|
8|1F4A0|💠|diamond with a dot|菱形加圓點|comic diamond dot geometric diamond with a dot diamond_shape_with_a_dot_inside 菱形加圓點 鑽石|
8|1F518|🔘|radio button|按鈕|button geometric radio radio button radio_button 按鈕 圓鈕 幾何|
8|1F533|🔳|white square button|白色方按鈕|button geometric outlined square white white square button white_square_button 白色方按鈕 按鈕 白色按鈕|
8|1F532|🔲|black square button|黑色方按鈕|black button geometric square black square button black_square_button 黑色方按鈕 按鈕 方形 方形按鈕|
9|1F3C1|🏁|chequered flag|終點旗|checkered chequered finish flag flags game race racing sport win chequered flag checkered_flag 終點旗 格子旗 賽車|
9|1F6A9|🚩|triangular flag|三角旗|construction flag golf post triangular triangular flag triangular_flag_on_post 三角旗 三角旗|
9|1F38C|🎌|crossed flags|紀念日|celebration cross crossed flags japanese crossed flags crossed_flags 紀念日 半程 旗 日本 盟友|
9|1F3F4|🏴|black flag|黑旗|black flag waving black flag black_flag 黑旗 揮黑旗|
9|1F3F3|🏳️|white flag|白旗|flag waving white white flag white_flag 白旗 搖白旗 豎白旗|
9|1F3F3-FE0F-200D-1F308|🏳️‍🌈|rainbow flag|彩虹旗|bisexual flag gay genderqueer glbt glbtq lesbian lgbt lgbtq lgbtqia pride queer rainbow trans transgender rainbow flag rainbow_flag 彩虹旗 lgbt 同志 跨性別 雙性|
9|1F3F3-FE0F-200D-26A7-FE0F|🏳️‍⚧️|transgender flag|跨性別旗|blue flag light pink transgender white transgender flag transgender_flag 跨性別旗 旗子 變性|
9|1F3F4-200D-2620-FE0F|🏴‍☠️|pirate flag|海盜旗|flag jolly pirate plunder roger treasure pirate flag pirate_flag 海盜旗 寶藏 掠奪 海盜 骷髏旗|
9|1F1E6-1F1E8|🇦🇨|flag: Ascension Island|旗子：阿森松島|ac flag flag: ascension island ascension_island 旗子：阿森松島 AC 旗子|
9|1F1E6-1F1E9|🇦🇩|flag: Andorra|旗子：安道爾|ad flag flag: andorra andorra 旗子：安道爾 AD 旗子|
9|1F1E6-1F1EA|🇦🇪|flag: United Arab Emirates|旗子：阿拉伯聯合大公國|ae flag flag: united arab emirates united_arab_emirates 旗子：阿拉伯聯合大公國 AE 旗子|
9|1F1E6-1F1EB|🇦🇫|flag: Afghanistan|旗子：阿富汗|af flag flag: afghanistan afghanistan 旗子：阿富汗 AF 旗子|
9|1F1E6-1F1EC|🇦🇬|flag: Antigua & Barbuda|旗子：安地卡及巴布達|ag flag flag: antigua & barbuda antigua_barbuda 旗子：安地卡及巴布達 AG 旗子|
9|1F1E6-1F1EE|🇦🇮|flag: Anguilla|旗子：安奎拉|ai flag flag: anguilla anguilla 旗子：安奎拉 AI 旗子|
9|1F1E6-1F1F1|🇦🇱|flag: Albania|旗子：阿爾巴尼亞|al flag flag: albania albania 旗子：阿爾巴尼亞 AL 旗子|
9|1F1E6-1F1F2|🇦🇲|flag: Armenia|旗子：亞美尼亞|am flag flag: armenia armenia 旗子：亞美尼亞 AM 旗子|
9|1F1E6-1F1F4|🇦🇴|flag: Angola|旗子：安哥拉|ao flag flag: angola angola 旗子：安哥拉 AO 旗子|
9|1F1E6-1F1F6|🇦🇶|flag: Antarctica|旗子：南極洲|aq flag flag: antarctica antarctica 旗子：南極洲 AQ 旗子|
9|1F1E6-1F1F7|🇦🇷|flag: Argentina|旗子：阿根廷|ar flag flag: argentina argentina 旗子：阿根廷 AR 旗子|
9|1F1E6-1F1F8|🇦🇸|flag: American Samoa|旗子：美屬薩摩亞|as flag flag: american samoa american_samoa 旗子：美屬薩摩亞 AS 旗子|
9|1F1E6-1F1F9|🇦🇹|flag: Austria|旗子：奧地利|at flag flag: austria austria 旗子：奧地利 AT 旗子|
9|1F1E6-1F1FA|🇦🇺|flag: Australia|旗子：澳洲|au flag flag: australia australia 旗子：澳洲 AU 旗子|
9|1F1E6-1F1FC|🇦🇼|flag: Aruba|旗子：荷屬阿魯巴|aw flag flag: aruba aruba 旗子：荷屬阿魯巴 AW 旗子|
9|1F1E6-1F1FD|🇦🇽|flag: Åland Islands|旗子：奧蘭群島|ax flag flag: åland islands aland_islands 旗子：奧蘭群島 AX 旗子|
9|1F1E6-1F1FF|🇦🇿|flag: Azerbaijan|旗子：亞塞拜然|az flag flag: azerbaijan azerbaijan 旗子：亞塞拜然 AZ 旗子|
9|1F1E7-1F1E6|🇧🇦|flag: Bosnia & Herzegovina|旗子：波士尼亞與赫塞哥維納|ba flag flag: bosnia & herzegovina bosnia_herzegovina 旗子：波士尼亞與赫塞哥維納 BA 旗子|
9|1F1E7-1F1E7|🇧🇧|flag: Barbados|旗子：巴貝多|bb flag flag: barbados barbados 旗子：巴貝多 BB 旗子|
9|1F1E7-1F1E9|🇧🇩|flag: Bangladesh|旗子：孟加拉|bd flag flag: bangladesh bangladesh 旗子：孟加拉 BD 旗子|
"""

private const val EMOJI_ROWS_8 = """
9|1F1E7-1F1EA|🇧🇪|flag: Belgium|旗子：比利時|be flag flag: belgium belgium 旗子：比利時 BE 旗子|
9|1F1E7-1F1EB|🇧🇫|flag: Burkina Faso|旗子：布吉納法索|bf flag flag: burkina faso burkina_faso 旗子：布吉納法索 BF 旗子|
9|1F1E7-1F1EC|🇧🇬|flag: Bulgaria|旗子：保加利亞|bg flag flag: bulgaria bulgaria 旗子：保加利亞 BG 旗子|
9|1F1E7-1F1ED|🇧🇭|flag: Bahrain|旗子：巴林|bh flag flag: bahrain bahrain 旗子：巴林 BH 旗子|
9|1F1E7-1F1EE|🇧🇮|flag: Burundi|旗子：蒲隆地|bi flag flag: burundi burundi 旗子：蒲隆地 BI 旗子|
9|1F1E7-1F1EF|🇧🇯|flag: Benin|旗子：貝南|bj flag flag: benin benin 旗子：貝南 BJ 旗子|
9|1F1E7-1F1F1|🇧🇱|flag: St. Barthélemy|旗子：聖巴瑟米|bl flag flag: st. barthélemy st_barthelemy 旗子：聖巴瑟米 BL 旗子|
9|1F1E7-1F1F2|🇧🇲|flag: Bermuda|旗子：百慕達|bm flag flag: bermuda bermuda 旗子：百慕達 BM 旗子|
9|1F1E7-1F1F3|🇧🇳|flag: Brunei|旗子：汶萊|bn flag flag: brunei brunei 旗子：汶萊 BN 旗子|
9|1F1E7-1F1F4|🇧🇴|flag: Bolivia|旗子：玻利維亞|bo flag flag: bolivia bolivia 旗子：玻利維亞 BO 旗子|
9|1F1E7-1F1F6|🇧🇶|flag: Caribbean Netherlands|旗子：荷蘭加勒比區|bq flag flag: caribbean netherlands caribbean_netherlands 旗子：荷蘭加勒比區 BQ 旗子|
9|1F1E7-1F1F7|🇧🇷|flag: Brazil|旗子：巴西|br flag flag: brazil brazil 旗子：巴西 BR 旗子|
9|1F1E7-1F1F8|🇧🇸|flag: Bahamas|旗子：巴哈馬|bs flag flag: bahamas bahamas 旗子：巴哈馬 BS 旗子|
9|1F1E7-1F1F9|🇧🇹|flag: Bhutan|旗子：不丹|bt flag flag: bhutan bhutan 旗子：不丹 BT 旗子|
9|1F1E7-1F1FB|🇧🇻|flag: Bouvet Island|旗子：布威島|bv flag flag: bouvet island bouvet_island 旗子：布威島 BV 旗子|
9|1F1E7-1F1FC|🇧🇼|flag: Botswana|旗子：波札那|bw flag flag: botswana botswana 旗子：波札那 BW 旗子|
9|1F1E7-1F1FE|🇧🇾|flag: Belarus|旗子：白俄羅斯|by flag flag: belarus belarus 旗子：白俄羅斯 BY 旗子|
9|1F1E7-1F1FF|🇧🇿|flag: Belize|旗子：貝里斯|bz flag flag: belize belize 旗子：貝里斯 BZ 旗子|
9|1F1E8-1F1E6|🇨🇦|flag: Canada|旗子：加拿大|ca flag flag: canada canada 旗子：加拿大 CA 旗子|
9|1F1E8-1F1E8|🇨🇨|flag: Cocos (Keeling) Islands|旗子：科克斯（基靈）群島|cc flag flag: cocos (keeling) islands cocos_islands 旗子：科克斯（基靈）群島 CC 旗子|
9|1F1E8-1F1E9|🇨🇩|flag: Congo - Kinshasa|旗子：剛果（金夏沙）|cd flag flag: congo - kinshasa congo_kinshasa 旗子：剛果（金夏沙） CD 旗子|
9|1F1E8-1F1EB|🇨🇫|flag: Central African Republic|旗子：中非共和國|cf flag flag: central african republic central_african_republic 旗子：中非共和國 CF 旗子|
9|1F1E8-1F1EC|🇨🇬|flag: Congo - Brazzaville|旗子：剛果（布拉薩）|cg flag flag: congo - brazzaville congo_brazzaville 旗子：剛果（布拉薩） CG 旗子|
9|1F1E8-1F1ED|🇨🇭|flag: Switzerland|旗子：瑞士|ch flag flag: switzerland switzerland 旗子：瑞士 CH 旗子|
9|1F1E8-1F1EE|🇨🇮|flag: Côte d’Ivoire|旗子：象牙海岸|ci flag flag: côte d’ivoire cote_divoire 旗子：象牙海岸 CI 旗子|
9|1F1E8-1F1F0|🇨🇰|flag: Cook Islands|旗子：庫克群島|ck flag flag: cook islands cook_islands 旗子：庫克群島 CK 旗子|
9|1F1E8-1F1F1|🇨🇱|flag: Chile|旗子：智利|cl flag flag: chile chile 旗子：智利 CL 旗子|
9|1F1E8-1F1F2|🇨🇲|flag: Cameroon|旗子：喀麥隆|cm flag flag: cameroon cameroon 旗子：喀麥隆 CM 旗子|
9|1F1E8-1F1F3|🇨🇳|flag: China|旗子：中國|cn flag flag: china 旗子：中國 CN 旗子|
9|1F1E8-1F1F4|🇨🇴|flag: Colombia|旗子：哥倫比亞|co flag flag: colombia colombia 旗子：哥倫比亞 CO 旗子|
9|1F1E8-1F1F5|🇨🇵|flag: Clipperton Island|旗子：克里派頓島|cp flag flag: clipperton island clipperton_island 旗子：克里派頓島 CP 旗子|
9|1F1E8-1F1F6|🇨🇶|flag: Sark|旗子：薩克島|cq flag flag: sark 旗子：薩克島 CQ 旗子|
9|1F1E8-1F1F7|🇨🇷|flag: Costa Rica|旗子：哥斯大黎加|cr flag flag: costa rica costa_rica 旗子：哥斯大黎加 CR 旗子|
9|1F1E8-1F1FA|🇨🇺|flag: Cuba|旗子：古巴|cu flag flag: cuba cuba 旗子：古巴 CU 旗子|
9|1F1E8-1F1FB|🇨🇻|flag: Cape Verde|旗子：維德角|cv flag flag: cape verde cape_verde 旗子：維德角 CV 旗子|
9|1F1E8-1F1FC|🇨🇼|flag: Curaçao|旗子：庫拉索|cw flag flag: curaçao curacao 旗子：庫拉索 CW 旗子|
9|1F1E8-1F1FD|🇨🇽|flag: Christmas Island|旗子：聖誕島|cx flag flag: christmas island christmas_island 旗子：聖誕島 CX 旗子|
9|1F1E8-1F1FE|🇨🇾|flag: Cyprus|旗子：賽普勒斯|cy flag flag: cyprus cyprus 旗子：賽普勒斯 CY 旗子|
9|1F1E8-1F1FF|🇨🇿|flag: Czechia|旗子：捷克|cz flag flag: czechia czech_republic 旗子：捷克 CZ 旗子|
9|1F1E9-1F1EA|🇩🇪|flag: Germany|旗子：德國|de flag flag: germany 旗子：德國 DE 旗子|
9|1F1E9-1F1EC|🇩🇬|flag: Diego Garcia|旗子：迪亞哥加西亞島|dg flag flag: diego garcia diego_garcia 旗子：迪亞哥加西亞島 DG 旗子|
9|1F1E9-1F1EF|🇩🇯|flag: Djibouti|旗子：吉布地|dj flag flag: djibouti djibouti 旗子：吉布地 DJ 旗子|
9|1F1E9-1F1F0|🇩🇰|flag: Denmark|旗子：丹麥|dk flag flag: denmark denmark 旗子：丹麥 DK 旗子|
9|1F1E9-1F1F2|🇩🇲|flag: Dominica|旗子：多米尼克|dm flag flag: dominica dominica 旗子：多米尼克 DM 旗子|
9|1F1E9-1F1F4|🇩🇴|flag: Dominican Republic|旗子：多明尼加共和國|do flag flag: dominican republic dominican_republic 旗子：多明尼加共和國 DO 旗子|
9|1F1E9-1F1FF|🇩🇿|flag: Algeria|旗子：阿爾及利亞|dz flag flag: algeria algeria 旗子：阿爾及利亞 DZ 旗子|
9|1F1EA-1F1E6|🇪🇦|flag: Ceuta & Melilla|旗子：休達與梅利利亞|ea flag flag: ceuta & melilla ceuta_melilla 旗子：休達與梅利利亞 EA 旗子|
9|1F1EA-1F1E8|🇪🇨|flag: Ecuador|旗子：厄瓜多|ec flag flag: ecuador ecuador 旗子：厄瓜多 EC 旗子|
9|1F1EA-1F1EA|🇪🇪|flag: Estonia|旗子：愛沙尼亞|ee flag flag: estonia estonia 旗子：愛沙尼亞 EE 旗子|
9|1F1EA-1F1EC|🇪🇬|flag: Egypt|旗子：埃及|eg flag flag: egypt egypt 旗子：埃及 EG 旗子|
9|1F1EA-1F1ED|🇪🇭|flag: Western Sahara|旗子：西撒哈拉|eh flag flag: western sahara western_sahara 旗子：西撒哈拉 EH 旗子|
9|1F1EA-1F1F7|🇪🇷|flag: Eritrea|旗子：厄利垂亞|er flag flag: eritrea eritrea 旗子：厄利垂亞 ER 旗子|
9|1F1EA-1F1F8|🇪🇸|flag: Spain|旗子：西班牙|es flag flag: spain 旗子：西班牙 ES 旗子|
9|1F1EA-1F1F9|🇪🇹|flag: Ethiopia|旗子：衣索比亞|et flag flag: ethiopia ethiopia 旗子：衣索比亞 ET 旗子|
9|1F1EA-1F1FA|🇪🇺|flag: European Union|旗子：歐盟|eu flag flag: european union european_union 旗子：歐盟 EU 旗子|
9|1F1EB-1F1EE|🇫🇮|flag: Finland|旗子：芬蘭|fi flag flag: finland finland 旗子：芬蘭 FI 旗子|
9|1F1EB-1F1EF|🇫🇯|flag: Fiji|旗子：斐濟|fj flag flag: fiji fiji 旗子：斐濟 FJ 旗子|
9|1F1EB-1F1F0|🇫🇰|flag: Falkland Islands|旗子：福克蘭群島|fk flag flag: falkland islands falkland_islands 旗子：福克蘭群島 FK 旗子|
9|1F1EB-1F1F2|🇫🇲|flag: Micronesia|旗子：密克羅尼西亞|fm flag flag: micronesia micronesia 旗子：密克羅尼西亞 FM 旗子|
9|1F1EB-1F1F4|🇫🇴|flag: Faroe Islands|旗子：法羅群島|fo flag flag: faroe islands faroe_islands 旗子：法羅群島 FO 旗子|
9|1F1EB-1F1F7|🇫🇷|flag: France|旗子：法國|fr flag flag: france 旗子：法國 FR 旗子|
9|1F1EC-1F1E6|🇬🇦|flag: Gabon|旗子：加彭|ga flag flag: gabon gabon 旗子：加彭 GA 旗子|
9|1F1EC-1F1E7|🇬🇧|flag: United Kingdom|旗子：英國|gb flag flag: united kingdom uk 旗子：英國 GB 旗子|
9|1F1EC-1F1E9|🇬🇩|flag: Grenada|旗子：格瑞那達|gd flag flag: grenada grenada 旗子：格瑞那達 GD 旗子|
9|1F1EC-1F1EA|🇬🇪|flag: Georgia|旗子：喬治亞|ge flag flag: georgia georgia 旗子：喬治亞 GE 旗子|
9|1F1EC-1F1EB|🇬🇫|flag: French Guiana|旗子：法屬圭亞那|gf flag flag: french guiana french_guiana 旗子：法屬圭亞那 GF 旗子|
9|1F1EC-1F1EC|🇬🇬|flag: Guernsey|旗子：根息|gg flag flag: guernsey guernsey 旗子：根息 GG 旗子|
9|1F1EC-1F1ED|🇬🇭|flag: Ghana|旗子：迦納|gh flag flag: ghana ghana 旗子：迦納 GH 旗子|
9|1F1EC-1F1EE|🇬🇮|flag: Gibraltar|旗子：直布羅陀|gi flag flag: gibraltar gibraltar 旗子：直布羅陀 GI 旗子|
9|1F1EC-1F1F1|🇬🇱|flag: Greenland|旗子：格陵蘭|gl flag flag: greenland greenland 旗子：格陵蘭 GL 旗子|
9|1F1EC-1F1F2|🇬🇲|flag: Gambia|旗子：甘比亞|gm flag flag: gambia gambia 旗子：甘比亞 GM 旗子|
9|1F1EC-1F1F3|🇬🇳|flag: Guinea|旗子：幾內亞|gn flag flag: guinea guinea 旗子：幾內亞 GN 旗子|
9|1F1EC-1F1F5|🇬🇵|flag: Guadeloupe|旗子：瓜地洛普|gp flag flag: guadeloupe guadeloupe 旗子：瓜地洛普 GP 旗子|
9|1F1EC-1F1F6|🇬🇶|flag: Equatorial Guinea|旗子：赤道幾內亞|gq flag flag: equatorial guinea equatorial_guinea 旗子：赤道幾內亞 GQ 旗子|
9|1F1EC-1F1F7|🇬🇷|flag: Greece|旗子：希臘|gr flag flag: greece greece 旗子：希臘 GR 旗子|
9|1F1EC-1F1F8|🇬🇸|flag: South Georgia & South Sandwich Islands|旗子：南喬治亞與南三明治群島|gs flag flag: south georgia & south sandwich islands south_georgia_south_sandwich_islands 旗子：南喬治亞與南三明治群島 GS 旗子|
9|1F1EC-1F1F9|🇬🇹|flag: Guatemala|旗子：瓜地馬拉|gt flag flag: guatemala guatemala 旗子：瓜地馬拉 GT 旗子|
9|1F1EC-1F1FA|🇬🇺|flag: Guam|旗子：關島|gu flag flag: guam guam 旗子：關島 GU 旗子|
9|1F1EC-1F1FC|🇬🇼|flag: Guinea-Bissau|旗子：幾內亞比索|gw flag flag: guinea-bissau guinea_bissau 旗子：幾內亞比索 GW 旗子|
9|1F1EC-1F1FE|🇬🇾|flag: Guyana|旗子：蓋亞那|gy flag flag: guyana guyana 旗子：蓋亞那 GY 旗子|
9|1F1ED-1F1F0|🇭🇰|flag: Hong Kong SAR China|旗子：中國香港特別行政區|hk flag flag: hong kong sar china hong_kong 旗子：中國香港特別行政區 HK 旗子|
9|1F1ED-1F1F2|🇭🇲|flag: Heard & McDonald Islands|旗子：赫德島及麥唐納群島|hm flag flag: heard & mcdonald islands heard_mcdonald_islands 旗子：赫德島及麥唐納群島 HM 旗子|
9|1F1ED-1F1F3|🇭🇳|flag: Honduras|旗子：宏都拉斯|hn flag flag: honduras honduras 旗子：宏都拉斯 HN 旗子|
9|1F1ED-1F1F7|🇭🇷|flag: Croatia|旗子：克羅埃西亞|hr flag flag: croatia croatia 旗子：克羅埃西亞 HR 旗子|
9|1F1ED-1F1F9|🇭🇹|flag: Haiti|旗子：海地|ht flag flag: haiti haiti 旗子：海地 HT 旗子|
9|1F1ED-1F1FA|🇭🇺|flag: Hungary|旗子：匈牙利|hu flag flag: hungary hungary 旗子：匈牙利 HU 旗子|
9|1F1EE-1F1E8|🇮🇨|flag: Canary Islands|旗子：加那利群島|ic flag flag: canary islands canary_islands 旗子：加那利群島 IC 旗子|
9|1F1EE-1F1E9|🇮🇩|flag: Indonesia|旗子：印尼|id flag flag: indonesia indonesia 旗子：印尼 ID 旗子|
9|1F1EE-1F1EA|🇮🇪|flag: Ireland|旗子：愛爾蘭|ie flag flag: ireland ireland 旗子：愛爾蘭 IE 旗子|
9|1F1EE-1F1F1|🇮🇱|flag: Israel|旗子：以色列|il flag flag: israel israel 旗子：以色列 IL 旗子|
9|1F1EE-1F1F2|🇮🇲|flag: Isle of Man|旗子：曼島|im flag flag: isle of man isle_of_man 旗子：曼島 IM 旗子|
9|1F1EE-1F1F3|🇮🇳|flag: India|旗子：印度|in flag flag: india india 旗子：印度 IN 旗子|
9|1F1EE-1F1F4|🇮🇴|flag: British Indian Ocean Territory|旗子：英屬印度洋領地|io flag flag: british indian ocean territory british_indian_ocean_territory 旗子：英屬印度洋領地 IO 旗子|
9|1F1EE-1F1F6|🇮🇶|flag: Iraq|旗子：伊拉克|iq flag flag: iraq iraq 旗子：伊拉克 IQ 旗子|
9|1F1EE-1F1F7|🇮🇷|flag: Iran|旗子：伊朗|ir flag flag: iran iran 旗子：伊朗 IR 旗子|
9|1F1EE-1F1F8|🇮🇸|flag: Iceland|旗子：冰島|is flag flag: iceland iceland 旗子：冰島 IS 旗子|
9|1F1EE-1F1F9|🇮🇹|flag: Italy|旗子：義大利|it flag flag: italy 旗子：義大利 IT 旗子|
9|1F1EF-1F1EA|🇯🇪|flag: Jersey|旗子：澤西島|je flag flag: jersey jersey 旗子：澤西島 JE 旗子|
9|1F1EF-1F1F2|🇯🇲|flag: Jamaica|旗子：牙買加|jm flag flag: jamaica jamaica 旗子：牙買加 JM 旗子|
9|1F1EF-1F1F4|🇯🇴|flag: Jordan|旗子：約旦|jo flag flag: jordan jordan 旗子：約旦 JO 旗子|
9|1F1EF-1F1F5|🇯🇵|flag: Japan|旗子：日本|jp flag flag: japan 旗子：日本 JP 旗子|
9|1F1F0-1F1EA|🇰🇪|flag: Kenya|旗子：肯亞|ke flag flag: kenya kenya 旗子：肯亞 KE 旗子|
9|1F1F0-1F1EC|🇰🇬|flag: Kyrgyzstan|旗子：吉爾吉斯|kg flag flag: kyrgyzstan kyrgyzstan 旗子：吉爾吉斯 KG 旗子|
9|1F1F0-1F1ED|🇰🇭|flag: Cambodia|旗子：柬埔寨|kh flag flag: cambodia cambodia 旗子：柬埔寨 KH 旗子|
9|1F1F0-1F1EE|🇰🇮|flag: Kiribati|旗子：吉里巴斯|ki flag flag: kiribati kiribati 旗子：吉里巴斯 KI 旗子|
9|1F1F0-1F1F2|🇰🇲|flag: Comoros|旗子：葛摩|km flag flag: comoros comoros 旗子：葛摩 KM 旗子|
9|1F1F0-1F1F3|🇰🇳|flag: St. Kitts & Nevis|旗子：聖克里斯多福及尼維斯|kn flag flag: st. kitts & nevis st_kitts_nevis 旗子：聖克里斯多福及尼維斯 KN 旗子|
9|1F1F0-1F1F5|🇰🇵|flag: North Korea|旗子：北韓|kp flag flag: north korea north_korea 旗子：北韓 KP 旗子|
9|1F1F0-1F1F7|🇰🇷|flag: South Korea|旗子：南韓|kr flag flag: south korea 旗子：南韓 KR 旗子|
9|1F1F0-1F1FC|🇰🇼|flag: Kuwait|旗子：科威特|kw flag flag: kuwait kuwait 旗子：科威特 KW 旗子|
9|1F1F0-1F1FE|🇰🇾|flag: Cayman Islands|旗子：開曼群島|ky flag flag: cayman islands cayman_islands 旗子：開曼群島 KY 旗子|
9|1F1F0-1F1FF|🇰🇿|flag: Kazakhstan|旗子：哈薩克|kz flag flag: kazakhstan kazakhstan 旗子：哈薩克 KZ 旗子|
9|1F1F1-1F1E6|🇱🇦|flag: Laos|旗子：寮國|la flag flag: laos laos 旗子：寮國 LA 旗子|
9|1F1F1-1F1E7|🇱🇧|flag: Lebanon|旗子：黎巴嫩|lb flag flag: lebanon lebanon 旗子：黎巴嫩 LB 旗子|
9|1F1F1-1F1E8|🇱🇨|flag: St. Lucia|旗子：聖露西亞|lc flag flag: st. lucia st_lucia 旗子：聖露西亞 LC 旗子|
9|1F1F1-1F1EE|🇱🇮|flag: Liechtenstein|旗子：列支敦斯登|li flag flag: liechtenstein liechtenstein 旗子：列支敦斯登 LI 旗子|
9|1F1F1-1F1F0|🇱🇰|flag: Sri Lanka|旗子：斯里蘭卡|lk flag flag: sri lanka sri_lanka 旗子：斯里蘭卡 LK 旗子|
9|1F1F1-1F1F7|🇱🇷|flag: Liberia|旗子：賴比瑞亞|lr flag flag: liberia liberia 旗子：賴比瑞亞 LR 旗子|
9|1F1F1-1F1F8|🇱🇸|flag: Lesotho|旗子：賴索托|ls flag flag: lesotho lesotho 旗子：賴索托 LS 旗子|
9|1F1F1-1F1F9|🇱🇹|flag: Lithuania|旗子：立陶宛|lt flag flag: lithuania lithuania 旗子：立陶宛 LT 旗子|
9|1F1F1-1F1FA|🇱🇺|flag: Luxembourg|旗子：盧森堡|lu flag flag: luxembourg luxembourg 旗子：盧森堡 LU 旗子|
9|1F1F1-1F1FB|🇱🇻|flag: Latvia|旗子：拉脫維亞|lv flag flag: latvia latvia 旗子：拉脫維亞 LV 旗子|
9|1F1F1-1F1FE|🇱🇾|flag: Libya|旗子：利比亞|ly flag flag: libya libya 旗子：利比亞 LY 旗子|
9|1F1F2-1F1E6|🇲🇦|flag: Morocco|旗子：摩洛哥|ma flag flag: morocco morocco 旗子：摩洛哥 MA 旗子|
9|1F1F2-1F1E8|🇲🇨|flag: Monaco|旗子：摩納哥|mc flag flag: monaco monaco 旗子：摩納哥 MC 旗子|
9|1F1F2-1F1E9|🇲🇩|flag: Moldova|旗子：摩爾多瓦|md flag flag: moldova moldova 旗子：摩爾多瓦 MD 旗子|
9|1F1F2-1F1EA|🇲🇪|flag: Montenegro|旗子：蒙特內哥羅|me flag flag: montenegro montenegro 旗子：蒙特內哥羅 ME 旗子|
9|1F1F2-1F1EB|🇲🇫|flag: St. Martin|旗子：法屬聖馬丁|mf flag flag: st. martin st_martin 旗子：法屬聖馬丁 MF 旗子|
9|1F1F2-1F1EC|🇲🇬|flag: Madagascar|旗子：馬達加斯加|mg flag flag: madagascar madagascar 旗子：馬達加斯加 MG 旗子|
9|1F1F2-1F1ED|🇲🇭|flag: Marshall Islands|旗子：馬紹爾群島|mh flag flag: marshall islands marshall_islands 旗子：馬紹爾群島 MH 旗子|
9|1F1F2-1F1F0|🇲🇰|flag: North Macedonia|旗子：北馬其頓|mk flag flag: north macedonia macedonia 旗子：北馬其頓 MK 旗子|
9|1F1F2-1F1F1|🇲🇱|flag: Mali|旗子：馬利|ml flag flag: mali mali 旗子：馬利 ML 旗子|
9|1F1F2-1F1F2|🇲🇲|flag: Myanmar (Burma)|旗子：緬甸|mm flag flag: myanmar (burma) myanmar 旗子：緬甸 MM 旗子|
9|1F1F2-1F1F3|🇲🇳|flag: Mongolia|旗子：蒙古|mn flag flag: mongolia mongolia 旗子：蒙古 MN 旗子|
9|1F1F2-1F1F4|🇲🇴|flag: Macao SAR China|旗子：中國澳門特別行政區|mo flag flag: macao sar china macau 旗子：中國澳門特別行政區 MO 旗子|
9|1F1F2-1F1F5|🇲🇵|flag: Northern Mariana Islands|旗子：北馬利安納群島|mp flag flag: northern mariana islands northern_mariana_islands 旗子：北馬利安納群島 MP 旗子|
9|1F1F2-1F1F6|🇲🇶|flag: Martinique|旗子：馬丁尼克|mq flag flag: martinique martinique 旗子：馬丁尼克 MQ 旗子|
9|1F1F2-1F1F7|🇲🇷|flag: Mauritania|旗子：茅利塔尼亞|mr flag flag: mauritania mauritania 旗子：茅利塔尼亞 MR 旗子|
9|1F1F2-1F1F8|🇲🇸|flag: Montserrat|旗子：蒙哲臘|ms flag flag: montserrat montserrat 旗子：蒙哲臘 MS 旗子|
9|1F1F2-1F1F9|🇲🇹|flag: Malta|旗子：馬爾他|mt flag flag: malta malta 旗子：馬爾他 MT 旗子|
9|1F1F2-1F1FA|🇲🇺|flag: Mauritius|旗子：模里西斯|mu flag flag: mauritius mauritius 旗子：模里西斯 MU 旗子|
9|1F1F2-1F1FB|🇲🇻|flag: Maldives|旗子：馬爾地夫|mv flag flag: maldives maldives 旗子：馬爾地夫 MV 旗子|
9|1F1F2-1F1FC|🇲🇼|flag: Malawi|旗子：馬拉威|mw flag flag: malawi malawi 旗子：馬拉威 MW 旗子|
9|1F1F2-1F1FD|🇲🇽|flag: Mexico|旗子：墨西哥|mx flag flag: mexico mexico 旗子：墨西哥 MX 旗子|
9|1F1F2-1F1FE|🇲🇾|flag: Malaysia|旗子：馬來西亞|my flag flag: malaysia malaysia 旗子：馬來西亞 MY 旗子|
9|1F1F2-1F1FF|🇲🇿|flag: Mozambique|旗子：莫三比克|mz flag flag: mozambique mozambique 旗子：莫三比克 MZ 旗子|
9|1F1F3-1F1E6|🇳🇦|flag: Namibia|旗子：納米比亞|na flag flag: namibia namibia 旗子：納米比亞 NA 旗子|
9|1F1F3-1F1E8|🇳🇨|flag: New Caledonia|旗子：新喀里多尼亞|nc flag flag: new caledonia new_caledonia 旗子：新喀里多尼亞 NC 旗子|
9|1F1F3-1F1EA|🇳🇪|flag: Niger|旗子：尼日|ne flag flag: niger niger 旗子：尼日 NE 旗子|
9|1F1F3-1F1EB|🇳🇫|flag: Norfolk Island|旗子：諾福克島|nf flag flag: norfolk island norfolk_island 旗子：諾福克島 NF 旗子|
9|1F1F3-1F1EC|🇳🇬|flag: Nigeria|旗子：奈及利亞|ng flag flag: nigeria nigeria 旗子：奈及利亞 NG 旗子|
9|1F1F3-1F1EE|🇳🇮|flag: Nicaragua|旗子：尼加拉瓜|ni flag flag: nicaragua nicaragua 旗子：尼加拉瓜 NI 旗子|
9|1F1F3-1F1F1|🇳🇱|flag: Netherlands|旗子：荷蘭|nl flag flag: netherlands netherlands 旗子：荷蘭 NL 旗子|
9|1F1F3-1F1F4|🇳🇴|flag: Norway|旗子：挪威|no flag flag: norway norway 旗子：挪威 NO 旗子|
9|1F1F3-1F1F5|🇳🇵|flag: Nepal|旗子：尼泊爾|np flag flag: nepal nepal 旗子：尼泊爾 NP 旗子|
9|1F1F3-1F1F7|🇳🇷|flag: Nauru|旗子：諾魯|nr flag flag: nauru nauru 旗子：諾魯 NR 旗子|
9|1F1F3-1F1FA|🇳🇺|flag: Niue|旗子：紐埃島|nu flag flag: niue niue 旗子：紐埃島 NU 旗子|
9|1F1F3-1F1FF|🇳🇿|flag: New Zealand|旗子：紐西蘭|nz flag flag: new zealand new_zealand 旗子：紐西蘭 NZ 旗子|
9|1F1F4-1F1F2|🇴🇲|flag: Oman|旗子：阿曼|om flag flag: oman oman 旗子：阿曼 OM 旗子|
9|1F1F5-1F1E6|🇵🇦|flag: Panama|旗子：巴拿馬|pa flag flag: panama panama 旗子：巴拿馬 PA 旗子|
9|1F1F5-1F1EA|🇵🇪|flag: Peru|旗子：秘魯|pe flag flag: peru peru 旗子：秘魯 PE 旗子|
9|1F1F5-1F1EB|🇵🇫|flag: French Polynesia|旗子：法屬玻里尼西亞|pf flag flag: french polynesia french_polynesia 旗子：法屬玻里尼西亞 PF 旗子|
9|1F1F5-1F1EC|🇵🇬|flag: Papua New Guinea|旗子：巴布亞紐幾內亞|pg flag flag: papua new guinea papua_new_guinea 旗子：巴布亞紐幾內亞 PG 旗子|
9|1F1F5-1F1ED|🇵🇭|flag: Philippines|旗子：菲律賓|ph flag flag: philippines philippines 旗子：菲律賓 PH 旗子|
9|1F1F5-1F1F0|🇵🇰|flag: Pakistan|旗子：巴基斯坦|pk flag flag: pakistan pakistan 旗子：巴基斯坦 PK 旗子|
9|1F1F5-1F1F1|🇵🇱|flag: Poland|旗子：波蘭|pl flag flag: poland poland 旗子：波蘭 PL 旗子|
9|1F1F5-1F1F2|🇵🇲|flag: St. Pierre & Miquelon|旗子：聖皮埃與密克隆群島|pm flag flag: st. pierre & miquelon st_pierre_miquelon 旗子：聖皮埃與密克隆群島 PM 旗子|
9|1F1F5-1F1F3|🇵🇳|flag: Pitcairn Islands|旗子：皮特肯群島|pn flag flag: pitcairn islands pitcairn_islands 旗子：皮特肯群島 PN 旗子|
9|1F1F5-1F1F7|🇵🇷|flag: Puerto Rico|旗子：波多黎各|pr flag flag: puerto rico puerto_rico 旗子：波多黎各 PR 旗子|
9|1F1F5-1F1F8|🇵🇸|flag: Palestinian Territories|旗子：巴勒斯坦自治區|ps flag flag: palestinian territories palestinian_territories 旗子：巴勒斯坦自治區 PS 旗子|
9|1F1F5-1F1F9|🇵🇹|flag: Portugal|旗子：葡萄牙|pt flag flag: portugal portugal 旗子：葡萄牙 PT 旗子|
9|1F1F5-1F1FC|🇵🇼|flag: Palau|旗子：帛琉|pw flag flag: palau palau 旗子：帛琉 PW 旗子|
9|1F1F5-1F1FE|🇵🇾|flag: Paraguay|旗子：巴拉圭|py flag flag: paraguay paraguay 旗子：巴拉圭 PY 旗子|
9|1F1F6-1F1E6|🇶🇦|flag: Qatar|旗子：卡達|qa flag flag: qatar qatar 旗子：卡達 QA 旗子|
9|1F1F7-1F1EA|🇷🇪|flag: Réunion|旗子：留尼旺|re flag flag: réunion reunion 旗子：留尼旺 RE 旗子|
9|1F1F7-1F1F4|🇷🇴|flag: Romania|旗子：羅馬尼亞|ro flag flag: romania romania 旗子：羅馬尼亞 RO 旗子|
9|1F1F7-1F1F8|🇷🇸|flag: Serbia|旗子：塞爾維亞|rs flag flag: serbia serbia 旗子：塞爾維亞 RS 旗子|
9|1F1F7-1F1FA|🇷🇺|flag: Russia|旗子：俄羅斯|ru flag flag: russia 旗子：俄羅斯 RU 旗子|
9|1F1F7-1F1FC|🇷🇼|flag: Rwanda|旗子：盧安達|rw flag flag: rwanda rwanda 旗子：盧安達 RW 旗子|
9|1F1F8-1F1E6|🇸🇦|flag: Saudi Arabia|旗子：沙烏地阿拉伯|sa flag flag: saudi arabia saudi_arabia 旗子：沙烏地阿拉伯 SA 旗子|
9|1F1F8-1F1E7|🇸🇧|flag: Solomon Islands|旗子：索羅門群島|sb flag flag: solomon islands solomon_islands 旗子：索羅門群島 SB 旗子|
9|1F1F8-1F1E8|🇸🇨|flag: Seychelles|旗子：塞席爾|sc flag flag: seychelles seychelles 旗子：塞席爾 SC 旗子|
9|1F1F8-1F1E9|🇸🇩|flag: Sudan|旗子：蘇丹|sd flag flag: sudan sudan 旗子：蘇丹 SD 旗子|
9|1F1F8-1F1EA|🇸🇪|flag: Sweden|旗子：瑞典|se flag flag: sweden sweden 旗子：瑞典 SE 旗子|
9|1F1F8-1F1EC|🇸🇬|flag: Singapore|旗子：新加坡|sg flag flag: singapore singapore 旗子：新加坡 SG 旗子|
9|1F1F8-1F1ED|🇸🇭|flag: St. Helena|旗子：聖赫勒拿島|sh flag flag: st. helena st_helena 旗子：聖赫勒拿島 SH 旗子|
9|1F1F8-1F1EE|🇸🇮|flag: Slovenia|旗子：斯洛維尼亞|si flag flag: slovenia slovenia 旗子：斯洛維尼亞 SI 旗子|
9|1F1F8-1F1EF|🇸🇯|flag: Svalbard & Jan Mayen|旗子：挪威屬斯瓦巴及尖棉|sj flag flag: svalbard & jan mayen svalbard_jan_mayen 旗子：挪威屬斯瓦巴及尖棉 SJ 旗子|
9|1F1F8-1F1F0|🇸🇰|flag: Slovakia|旗子：斯洛伐克|sk flag flag: slovakia slovakia 旗子：斯洛伐克 SK 旗子|
9|1F1F8-1F1F1|🇸🇱|flag: Sierra Leone|旗子：獅子山|sl flag flag: sierra leone sierra_leone 旗子：獅子山 SL 旗子|
9|1F1F8-1F1F2|🇸🇲|flag: San Marino|旗子：聖馬利諾|sm flag flag: san marino san_marino 旗子：聖馬利諾 SM 旗子|
9|1F1F8-1F1F3|🇸🇳|flag: Senegal|旗子：塞內加爾|sn flag flag: senegal senegal 旗子：塞內加爾 SN 旗子|
9|1F1F8-1F1F4|🇸🇴|flag: Somalia|旗子：索馬利亞|so flag flag: somalia somalia 旗子：索馬利亞 SO 旗子|
9|1F1F8-1F1F7|🇸🇷|flag: Suriname|旗子：蘇利南|sr flag flag: suriname suriname 旗子：蘇利南 SR 旗子|
9|1F1F8-1F1F8|🇸🇸|flag: South Sudan|旗子：南蘇丹|ss flag flag: south sudan south_sudan 旗子：南蘇丹 SS 旗子|
9|1F1F8-1F1F9|🇸🇹|flag: São Tomé & Príncipe|旗子：聖多美普林西比|st flag flag: são tomé & príncipe sao_tome_principe 旗子：聖多美普林西比 ST 旗子|
9|1F1F8-1F1FB|🇸🇻|flag: El Salvador|旗子：薩爾瓦多|sv flag flag: el salvador el_salvador 旗子：薩爾瓦多 SV 旗子|
9|1F1F8-1F1FD|🇸🇽|flag: Sint Maarten|旗子：荷屬聖馬丁|sx flag flag: sint maarten sint_maarten 旗子：荷屬聖馬丁 SX 旗子|
9|1F1F8-1F1FE|🇸🇾|flag: Syria|旗子：敘利亞|sy flag flag: syria syria 旗子：敘利亞 SY 旗子|
9|1F1F8-1F1FF|🇸🇿|flag: Eswatini|旗子：史瓦帝尼|sz flag flag: eswatini swaziland 旗子：史瓦帝尼 SZ 旗子|
9|1F1F9-1F1E6|🇹🇦|flag: Tristan da Cunha|旗子：特里斯坦達庫尼亞群島|ta flag flag: tristan da cunha tristan_da_cunha 旗子：特里斯坦達庫尼亞群島 TA 旗子|
9|1F1F9-1F1E8|🇹🇨|flag: Turks & Caicos Islands|旗子：土克斯及開科斯群島|tc flag flag: turks & caicos islands turks_caicos_islands 旗子：土克斯及開科斯群島 TC 旗子|
9|1F1F9-1F1E9|🇹🇩|flag: Chad|旗子：查德|td flag flag: chad chad 旗子：查德 TD 旗子|
9|1F1F9-1F1EB|🇹🇫|flag: French Southern Territories|旗子：法屬南部屬地|tf flag flag: french southern territories french_southern_territories 旗子：法屬南部屬地 TF 旗子|
9|1F1F9-1F1EC|🇹🇬|flag: Togo|旗子：多哥|tg flag flag: togo togo 旗子：多哥 TG 旗子|
9|1F1F9-1F1ED|🇹🇭|flag: Thailand|旗子：泰國|th flag flag: thailand thailand 旗子：泰國 TH 旗子|
9|1F1F9-1F1EF|🇹🇯|flag: Tajikistan|旗子：塔吉克|tj flag flag: tajikistan tajikistan 旗子：塔吉克 TJ 旗子|
9|1F1F9-1F1F0|🇹🇰|flag: Tokelau|旗子：托克勞群島|tk flag flag: tokelau tokelau 旗子：托克勞群島 TK 旗子|
9|1F1F9-1F1F1|🇹🇱|flag: Timor-Leste|旗子：東帝汶|tl flag flag: timor-leste timor_leste 旗子：東帝汶 TL 旗子|
9|1F1F9-1F1F2|🇹🇲|flag: Turkmenistan|旗子：土庫曼|tm flag flag: turkmenistan turkmenistan 旗子：土庫曼 TM 旗子|
9|1F1F9-1F1F3|🇹🇳|flag: Tunisia|旗子：突尼西亞|tn flag flag: tunisia tunisia 旗子：突尼西亞 TN 旗子|
9|1F1F9-1F1F4|🇹🇴|flag: Tonga|旗子：東加|to flag flag: tonga tonga 旗子：東加 TO 旗子|
9|1F1F9-1F1F7|🇹🇷|flag: Türkiye|旗子：土耳其|tr flag flag: türkiye 旗子：土耳其 TR 旗子|
9|1F1F9-1F1F9|🇹🇹|flag: Trinidad & Tobago|旗子：千里達及托巴哥|tt flag flag: trinidad & tobago trinidad_tobago 旗子：千里達及托巴哥 TT 旗子|
9|1F1F9-1F1FB|🇹🇻|flag: Tuvalu|旗子：吐瓦魯|tv flag flag: tuvalu tuvalu 旗子：吐瓦魯 TV 旗子|
9|1F1F9-1F1FC|🇹🇼|flag: Taiwan|旗子：台灣|tw flag flag: taiwan taiwan 旗子：台灣 TW 旗子|
9|1F1F9-1F1FF|🇹🇿|flag: Tanzania|旗子：坦尚尼亞|tz flag flag: tanzania tanzania 旗子：坦尚尼亞 TZ 旗子|
9|1F1FA-1F1E6|🇺🇦|flag: Ukraine|旗子：烏克蘭|ua flag flag: ukraine ukraine 旗子：烏克蘭 UA 旗子|
9|1F1FA-1F1EC|🇺🇬|flag: Uganda|旗子：烏干達|ug flag flag: uganda uganda 旗子：烏干達 UG 旗子|
9|1F1FA-1F1F2|🇺🇲|flag: U.S. Outlying Islands|旗子：美國本土外小島嶼|um flag flag: u.s. outlying islands us_outlying_islands 旗子：美國本土外小島嶼 UM 旗子|
9|1F1FA-1F1F3|🇺🇳|flag: United Nations|旗子：聯合國|un flag flag: united nations united_nations 旗子：聯合國 UN 旗子|
9|1F1FA-1F1F8|🇺🇸|flag: United States|旗子：美國|us flag flag: united states 旗子：美國 US 旗子|
9|1F1FA-1F1FE|🇺🇾|flag: Uruguay|旗子：烏拉圭|uy flag flag: uruguay uruguay 旗子：烏拉圭 UY 旗子|
9|1F1FA-1F1FF|🇺🇿|flag: Uzbekistan|旗子：烏茲別克|uz flag flag: uzbekistan uzbekistan 旗子：烏茲別克 UZ 旗子|
9|1F1FB-1F1E6|🇻🇦|flag: Vatican City|旗子：梵蒂岡|va flag flag: vatican city vatican_city 旗子：梵蒂岡 VA 旗子|
9|1F1FB-1F1E8|🇻🇨|flag: St. Vincent & Grenadines|旗子：聖文森及格瑞那丁|vc flag flag: st. vincent & grenadines st_vincent_grenadines 旗子：聖文森及格瑞那丁 VC 旗子|
9|1F1FB-1F1EA|🇻🇪|flag: Venezuela|旗子：委內瑞拉|ve flag flag: venezuela venezuela 旗子：委內瑞拉 VE 旗子|
9|1F1FB-1F1EC|🇻🇬|flag: British Virgin Islands|旗子：英屬維京群島|vg flag flag: british virgin islands british_virgin_islands 旗子：英屬維京群島 VG 旗子|
9|1F1FB-1F1EE|🇻🇮|flag: U.S. Virgin Islands|旗子：美屬維京群島|vi flag flag: u.s. virgin islands us_virgin_islands 旗子：美屬維京群島 VI 旗子|
9|1F1FB-1F1F3|🇻🇳|flag: Vietnam|旗子：越南|vn flag flag: vietnam vietnam 旗子：越南 VN 旗子|
9|1F1FB-1F1FA|🇻🇺|flag: Vanuatu|旗子：萬那杜|vu flag flag: vanuatu vanuatu 旗子：萬那杜 VU 旗子|
9|1F1FC-1F1EB|🇼🇫|flag: Wallis & Futuna|旗子：瓦利斯群島和富圖那群島|wf flag flag: wallis & futuna wallis_futuna 旗子：瓦利斯群島和富圖那群島 WF 旗子|
9|1F1FC-1F1F8|🇼🇸|flag: Samoa|旗子：薩摩亞|ws flag flag: samoa samoa 旗子：薩摩亞 WS 旗子|
9|1F1FD-1F1F0|🇽🇰|flag: Kosovo|旗子：科索沃|xk flag flag: kosovo kosovo 旗子：科索沃 XK 旗子|
9|1F1FE-1F1EA|🇾🇪|flag: Yemen|旗子：葉門|ye flag flag: yemen yemen 旗子：葉門 YE 旗子|
9|1F1FE-1F1F9|🇾🇹|flag: Mayotte|旗子：馬約特島|yt flag flag: mayotte mayotte 旗子：馬約特島 YT 旗子|
9|1F1FF-1F1E6|🇿🇦|flag: South Africa|旗子：南非|za flag flag: south africa south_africa 旗子：南非 ZA 旗子|
9|1F1FF-1F1F2|🇿🇲|flag: Zambia|旗子：尚比亞|zm flag flag: zambia zambia 旗子：尚比亞 ZM 旗子|
9|1F1FF-1F1FC|🇿🇼|flag: Zimbabwe|旗子：辛巴威|zw flag flag: zimbabwe zimbabwe 旗子：辛巴威 ZW 旗子|
9|1F3F4-E0067-E0062-E0065-E006E-E0067-E007F|🏴󠁧󠁢󠁥󠁮󠁧󠁿|flag: England|旗子：英格蘭|flag gbeng flag: england england 旗子：英格蘭 gbeng 旗子|
9|1F3F4-E0067-E0062-E0073-E0063-E0074-E007F|🏴󠁧󠁢󠁳󠁣󠁴󠁿|flag: Scotland|旗子：蘇格蘭|flag gbsct flag: scotland scotland 旗子：蘇格蘭 gbsct 旗子|
9|1F3F4-E0067-E0062-E0077-E006C-E0073-E007F|🏴󠁧󠁢󠁷󠁬󠁳󠁿|flag: Wales|旗子：威爾斯|flag gbwls flag: wales wales 旗子：威爾斯 gbwls 旗子|
"""

private val EMOJI_ROWS_RAW: String =
    EMOJI_ROWS_0 +
    EMOJI_ROWS_1 +
    EMOJI_ROWS_2 +
    EMOJI_ROWS_3 +
    EMOJI_ROWS_4 +
    EMOJI_ROWS_5 +
    EMOJI_ROWS_6 +
    EMOJI_ROWS_7 +
    EMOJI_ROWS_8

/**
 * 一個表情。[tokens] 是給搜尋用的空白分隔字串（英文名、中文 tags、膚色變體
 * 不進 tokens——變體由 [skins] 單獨帶）；[skins] 是 `hexcode:glyph` 清單。
 */
data class EmojiEntry(
    val group: Int,
    val hexcode: String,
    val glyph: String,
    val nameEn: String,
    val nameZh: String,
    val tokens: String,
    val skins: List<Pair<String, String>>,
)

internal object EmojiTable {
    val all: List<EmojiEntry> by lazy {
        EMOJI_ROWS_RAW.lineSequence().filter { it.isNotBlank() }.map { line ->
            val f = line.split('|')
            val skins = if (f.size > 6 && f[6].isNotEmpty()) {
                f[6].split(' ').mapNotNull {
                    val i = it.indexOf(':')
                    if (i <= 0) null else it.substring(0, i) to it.substring(i + 1)
                }
            } else emptyList()
            EmojiEntry(
                group = f[0].toIntOrNull() ?: 0,
                hexcode = f.getOrElse(1) { "" },
                glyph = f.getOrElse(2) { "" },
                nameEn = f.getOrElse(3) { "" },
                nameZh = f.getOrElse(4) { "" },
                tokens = f.getOrElse(5) { "" }.lowercase(),
                skins = skins,
            )
        }.toList()
    }

    /** 依 group 分組，維持表內順序（表已按 group 排過）。 */
    val byGroup: Map<Int, List<EmojiEntry>> by lazy { all.groupBy { it.group } }
}
