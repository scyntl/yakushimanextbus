package yakushimalife.yakushimanextbus;


import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.yakushimalife.yakushimanextbus.R;

public class StopListActivity extends NextBusActivity {    


	private TextView tvDisplayStopList;
	private final String StopListString="<b><font color =#11aa77>1. 永田 Nagata</font></b>" +
			"<br>&nbsp 2. 永田入口 Nagata Iriguchi" +
			"<br>&nbsp 3. エビス町 Ebisumachi" +
			"<br><b>4. 田舎浜 Inakahama</b>" +
			"<br>&nbsp 5. 中野橋 Nakanobashi" +
			"<br>&nbsp 6. 中野口 Nakanoguchi" +
			"<br>&nbsp 7. 四ツ瀬 Yotsuse" +
			"<br><b>8. 吉田 Yoshida</b>" +
			"<br>&nbsp 9. 白河 Shirakawa" +
			"<br>&nbsp 10. 大浦 Oura" +
			"<br><b><font color =#11aa77>11. 一奏 Issou</font></b>" +
			"<br>&nbsp 12. 一奏入口 Issou Iriguchi" +
			"<br>&nbsp 13. 矢筈 Yahazu" +
			"<br>&nbsp 14. 志戸小 Shitoko" +
			"<br>&nbsp 15. 泊川 Tomarigawa" +
			"<br>&nbsp 16. 振腰橋 Furikoshibashi" +
			"<br><b>17. 深川 Fukagawa</b>" +
			"<br>&nbsp 18. 屋久電前 Yakuden-Mae" +
			"<br>&nbsp 19. シーサイドホテル Seaside Hotel*" +
			"<br><b><font color =#11aa77>20. 宮之浦港 Miyanoura Port</font></b>" +
			"<br>&nbsp 21. 宮之浦港入口 Miyanourako Iriguchi" +
			"<br>&nbsp 22. 登上 Noboriagari" +
			"<br><b><font color =#11aa77>23. 宮之浦 Miyanoura</font></b>" +
			"<br>&nbsp 24. 上屋久町役場 Kamiyakucho Yakuba" +
			"<br><b>25. 小原町 Oharamachi</b>" +
			"<br><big>--<u>Shiratani Line</u>--</big>" +
			"<br>&nbsp &nbsp &nbsp <b>26.  牛床公園　Ushitoko Koen</b>" +
			"<br>&nbsp &nbsp &nbsp 27. 森の展望台 Mori-no-Tenbodai" +
			"<br>&nbsp &nbsp &nbsp 28. 雲の展望台 Kumo-no-Tenbodai" +
			"<br>&nbsp &nbsp &nbsp <b><font color =#11aa77>29. 白谷雲水峡 Shiratani Unsuikyo</font></b>" +
			"<br><b>30. Aコープ前 A-Coop Mae</b>" +
			"<br>&nbsp 31. 宮浦小前 Miyaurasho-Mae (Elementary School)" +
			"<br>&nbsp 32. 高校前 Koko-Mae (High School)" +
			"<br>&nbsp 33. 営団 Eidan" +
			"<br><b>34. 旭町 Asahimachi</b>" +
			"<br>&nbsp 35. 鳥越 Torigoe" +
			"<br>&nbsp 36. 楠川入口 Kusugawa Iriguchi" +
			"<br><b> 37. 楠川 Kusugawa</b>" +
			"<br>&nbsp 38. 湯ノ川温泉 Yunokawa Onsen" +
			"<br>&nbsp 39. 下牧野 Shimomakino" +
			"<br>&nbsp 40. 牧ノ北 Makinokawa" +
			"<br><b>&nbsp 41. 椨側 Tabugawa</b>" +
			"<br>&nbsp 42. 男川 Otokogawa" +
			"<br>&nbsp 43. 西小瀬田 Nishikoseda" +
			"<br><b>44. 小瀬田 Koseda</b>" +
			"<br>&nbsp 45. 女川 Onnagawa" +
			"<br>&nbsp 46. 診療所前 Shinryojo-Mae (Clinic)" +
			"<br>&nbsp 47. 長峰北 Ngamine-Kita" +
			"<br><b><font color =#11aa77>48. 空港前 Kukomae</font></b>" +
			"<br><b>49. 空港 Airport</b>" +
			"<br>&nbsp 50. 長峰 Nagamine" +
			"<br>&nbsp 51. 大川 Okawa" +
			"<br><b>&nbsp 52. 早崎 Hayasaki</b>" +
			"<br>&nbsp 53. 高見橋 Takamibashi" +
			"<br>&nbsp 54. 農道入口 Nodo Iriguchi" +
			"<br>&nbsp 55. 大迫 Osako" +
			"<br><b>&nbsp 56. 長久保 Nagakubo</b>" +
			"<br>&nbsp 57. 桑野 Kuwano" +
			"<br>&nbsp 58. 竜天 Ryuten" +
			"<br><b>&nbsp 59. 舟行 Funayuki</b>" +
			"<br>&nbsp 60. 磯辺 Isobe" +
			"<br>&nbsp 61. 中央 Chuo" +
			"<br><b>62. 合庁前 Gochomae</b>"+
			"<br>&nbsp 63. 警察署前 Keisatsusho-Mae (Police)"+
			"<br><b>64. 安房港 Anbo Port</b>"+
			"<br>&nbsp 65. 仲医院前 Naka Iin-Mae (Clinic)"+
			"<br><b><font color =#11aa77>66. 安房 Anbo</font></b>" +
			"<br><b>&nbsp 67. 牧野 Makino</b>" +
			"<br><b><font color =#11aa77>68. 屋久杉自然館 Yakusugi Shizenkan (Museum)</font></b>" +
			"<br><big>--<u>Arakawa Lines</u>--</big>" +
			"<br>&nbsp &nbsp &nbsp 69. 荒川三叉路 Arakawa Sansaro" +
			"<br>&nbsp &nbsp &nbsp <b><font color =#11aa77>70. 荒川登山口 Arakawa Tozanguchi (Trail Head)</font></b>" +
			"<br>&nbsp &nbsp &nbsp <b><font color =#11aa77>71. ヤクスギランド Yausugiland</font></b>" +
			"<br>&nbsp &nbsp &nbsp <b><font color =#11aa77>72. 紀元杉 Kigensugi</font></b>"+
			"<br><b>73. 盛久神社 Morihisa Jinja (Shrine)</b>"+
			"<br>&nbsp 74. 春田 Haruta"+
			"<br>&nbsp 75. 横峯 Yokomine"+
			"<br>&nbsp 76. 中字 Nakaaza" +
			"<br>&nbsp 77. 栄町 Sakaemachi" +
			"<br><b>78. 平野 Hirano</b>" +
			"<br>&nbsp 79. 樋之口 Hinokuchi" +
			"<br>&nbsp 80. 焼酎川 Shochugawa" +
			"<br>&nbsp 81. 中橋 Nakabashi" +
			"<br>&nbsp 82. 小田汲 Odakumi"+
			"<br>&nbsp 83. 高平 Takahira"+
			"<br>&nbsp 84. ホトー川 Hotogawa"+
			"<br><b>85. 麦生 Mugio</b>"+
			"<br><b>86. ボタニカルリサーチパークBotanical Research Park</b>" +
			"<br>&nbsp 87. 鯛ノ川 Tainokawa" +
			"<br>&nbsp 88. 原入口 Hara Iriguchi" +
			"<br><b>&nbsp 89. 原 Hara</b>" +
			"<br>&nbsp 90. 神山 Kamiyama" +
			"<br>&nbsp 91. 泥淵川 Dorobuchigawa" +
			"<br>&nbsp 92. 中野 Nakano"+
			"<br>&nbsp 93. 二又川 Futamatagawa"+
			"<br><b><font color =#11aa77>94. 尾之間 Onoaida</font></b>"+
			"<br>&nbsp 95. 尾之間中央 Onoaida Chuo" +
			"<br>&nbsp 96. 尾之間医院 Onoaida Iin (Cinic)" +
			"<br><b>97. JRホテル JR Hotel</b>" +
			"<br>&nbsp 98. 尾之間温泉入口 Onoaida Onsen Iriguchi"+
			"<br><b>99. いわさきホテル Iwasaki Hotel</b>" +
			"<br>&nbsp 100. ホテル入口 Hotel Iriguchi" +
			"<br>&nbsp 101. 矢石 Yaishi" +
			"<br><b>102. 小島 Koshima</b>" +
			"<br>&nbsp 103. 岳南中前 Gakunan Chu-Mae (Jr. High School)" +
			"<br>&nbsp 104. 彼岸野 Higanno" +
			"<br>&nbsp 105. 恋泊 Koidomari"+
			"<br>&nbsp 106. 上ノ牧 Kaminomaki"+
			"<br>&nbsp 107. 平内入口 Hirauchi Iriguchi"+
			"<br>&nbsp 108. 八幡小前 Hachiman Sho-Mae (Elementary School)"+
			"<br>&nbsp 109. 八幡神社 Hachiman Jinja (Shrine)" +
			"<br>&nbsp 110. 平内 Hirauchi" +
			"<br>&nbsp 111. 大崎橋 Osakibashi" +
			"<br><b>112. 平内海中温泉 Hirauchi Kaichu Onsen</b>" +
			"<br>&nbsp 113. 西開墾 Nishikaikon" +
			"<br><b>114. 湯泊 Yudomari</b>" +
			"<br>&nbsp 115. 夕陽ヶ丘 Yuhigaoka"+
			"<br>&nbsp 116. 湯泊西 Yudomari-Nishi"+
			"<br>&nbsp 117. 花揚川 Hanaagegawa"+
			"<br>&nbsp 118. 旭 Asahi"+
			"<br>&nbsp 119. 城下 Shiroshita" +
			"<br>&nbsp 120. 平家ノ城 Heikenojo" +
			"<br>&nbsp 121. 黒崎 Kurosaki" +
			"<br>&nbsp 122. 綱掛橋 Tsunakakebashi" +
			"<br><b>123. 中間 Nakama</b>" +
			"<br>&nbsp 124. 栗生入口 Kurio Iriguchi" +
			"<br><font color =#11aa77>&nbsp 125. 栗生 Kurio</font>"+
			"<br>&nbsp 126. 栗生小前 Kurio Sho-Mae (Elementary School)"+
			"<br><b>127. 栗生橋 Kuriobashi</b>"+
			"<br>&nbsp 128. 青少年村 Seishonen Mura"+
			"<br><b><font color =#11aa77>129. 大川の滝 Oko-no-Taki Waterfall</font></b>" +
			"<br><br><br>" +
			
			"<b><u>yakushimalife fares:</u></b><br>"+
			"<br><b><font color =#11aa77>1. 永田 Nagata</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;510" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br><b><font color =#11aa77>11. 一奏 Issou</font></b>" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;500" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br><b><font color =#11aa77>20. 宮之浦港 Miyanoura Port</font></b>" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;510" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>48. 空港前 Kukomae</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;410" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>66. 安房 Anbo</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑<br>&nbsp　&nbsp　630yen" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color =#11aa77>94. 尾之間 Onoaida</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;300" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color =#11aa77>110. 平内 Hirauchi</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;490" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color =#11aa77>125. 栗生 Kurio</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;260" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color =#11aa77>129. 大川の滝 Oko-no-Taki Waterfall</font></b>" + 
			"<br><br><br>" + 
			"<br><b><font color =#11aa77>1. 永田 Nagata</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;510" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br><b><font color =#11aa77>20. 宮之浦港 Miyanoura Port</font></b>" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;530" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>29. 白谷雲水峡 Shiratani Unsuikyo</font></b>" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;500" + 
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br><b><font color =#11aa77>23. 宮之浦 Miyanoura</font></b>" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;790" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>66. 安房 Anbo</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;220" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>68. 屋久杉自然館 Yakusugi Shizenkan (Museum)</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;590" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>71. ヤクスギランド Yausugiland</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;300" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>72. 紀元杉 Kigensugi</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;890" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>68. 屋久杉自然館 Yakusugi Shizenkan (Museum)</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;850 (&yen;670+&yen;180 surcharge from March thru November)" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>70. 荒川登山口 Arakawa Tozanguchi (Trail Head)</font></b>" + 
			"<br><br><br>" + 
			"<br><b><font color=#11aa77>66. 安房 Anbo</font></b>" +  
			"<br>&nbsp　&nbsp　&nbsp　↓↑" +
			"<br>&nbsp　&nbsp　&yen;1280" +
			"<br>&nbsp　&nbsp　&nbsp　↓↑" + 
			"<br><b><font color=#11aa77>129. 大川の滝 Oko-no-Taki Waterfall</font></b>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stoplist);

		tvDisplayStopList = (TextView) findViewById(R.id.tvstoplistcontent);
		tvDisplayStopList.setText(Html.fromHtml(StopListString));
		
//		tvDisplayStopList.setText(Html.fromHtml(getResources().getString(R.string.stoplistcontent)));
	}
}

