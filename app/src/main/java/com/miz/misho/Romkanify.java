package com.miz.misho;

import com.miz.misho.Exceptions.InvalidRomaji;

import java.util.HashMap;
import java.util.Arrays;

public class Romkanify {

    private HashMap<String, String> Romkana;
    private HashMap<Character, Character> htk;

    private RomajiTrie RT;

    private class TNode {
        private HashMap<Character, TNode> children;
        private String word;

        public HashMap<Character, TNode> getChildren() {
            return this.children;
        }

        public String getWord() {
            return this.word;
        }

        public TNode(String prefix) {
            children = new HashMap<>();
            word = prefix;
        }

        public TNode() {
            children = new HashMap<>();
        }
    }

    private class RomajiTrie {
        private TNode root;

        private RomajiTrie(String[] keyset) {
            root = new TNode();
            for(String s : keyset) {
                insert(s);
            }
        }

        public void insert(String s) {
            TNode current = root;
            for(int i = 0; i < s.length(); i++) {
                if(current.getChildren().containsKey(s.charAt(i))){
                    current = current.getChildren().get(s.charAt(i));
                } else {
                    TNode t = new TNode(s.substring(0, i));
                    current.getChildren().put(s.charAt(i), t);
                    current = current.getChildren().get(s.charAt(i));
                }
            }

        }

        public int repeatSearch(String substrings) {
            String substring;
            if(substrings.length() > 3) {
                substring = substrings.substring(0, 3);
            } else
                substring = substrings;
            for(int i = 0; i < 3; i++) {
                TNode troot = root;
                if(i == substring.length())
                    break;
                for(int j = 0; j < substring.length() - i; j++) {
                    if(troot.getChildren().containsKey(substring.charAt(j))) {
                        troot = troot.getChildren().get(substring.charAt(j));
                        if (j == substring.length() - i - 1) {
                            return j+1;
                        }
                    } else {
                        break;
                    }
                }
            }
            return -1;
        }
    }

    public Romkanify(){
        Romkana = new HashMap<>();
        //build table
        //kana + dakuten + handakuten
        Romkana.put("a", "あ");
        Romkana.put("i", "い");
        Romkana.put("u", "う");
        Romkana.put("e", "え");
        Romkana.put("o", "お");

        Romkana.put("ka", "か");
        Romkana.put("ki", "き");
        Romkana.put("ku", "く");
        Romkana.put("ke", "け");
        Romkana.put("ko", "こ");

        Romkana.put("ga", "が");
        Romkana.put("gi", "ぎ");
        Romkana.put("gu", "ぐ");
        Romkana.put("ge", "げ");
        Romkana.put("go", "ご");

        Romkana.put("za", "ざ");
        Romkana.put("ji", "じ");
        Romkana.put("zu", "ず");
        Romkana.put("ze", "ぜ");
        Romkana.put("zo", "ぞ");

        Romkana.put("sa", "さ");
        Romkana.put("shi", "し");
        Romkana.put("su", "す");
        Romkana.put("se", "せ");
        Romkana.put("so", "そ");

        Romkana.put("ta", "た");
        Romkana.put("chi", "ち");
        Romkana.put("tsu", "つ");
        Romkana.put("te", "て");
        Romkana.put("to", "と");

        Romkana.put("da", "だ");
        Romkana.put("dzi", "ぢ");
        Romkana.put("dzu", "づ");
        Romkana.put("de", "で");
        Romkana.put("do", "ど");

        Romkana.put("na", "な");
        Romkana.put("ni", "に");
        Romkana.put("nu", "ぬ");
        Romkana.put("ne", "ね");
        Romkana.put("no", "の");

        Romkana.put("ba", "ば");
        Romkana.put("bi", "び");
        Romkana.put("bu", "ぶ");
        Romkana.put("be", "べ");
        Romkana.put("bo", "ぼ");

        Romkana.put("ha", "は");
        Romkana.put("hi", "ひ");
        Romkana.put("fu", "ふ");
        Romkana.put("he", "へ");
        Romkana.put("ho", "ほ");

        Romkana.put("pa", "ぱ");
        Romkana.put("pi", "ぴ");
        Romkana.put("pu", "ぷ");
        Romkana.put("pe", "ぺ");
        Romkana.put("po", "ぽ");

        Romkana.put("ma", "ま");
        Romkana.put("mi", "み");
        Romkana.put("mu", "む");
        Romkana.put("me", "め");
        Romkana.put("mo", "も");

        Romkana.put("ra", "ら");
        Romkana.put("ri", "り");
        Romkana.put("ru", "る");
        Romkana.put("re", "れ");
        Romkana.put("ro", "ろ");

        Romkana.put("ya", "や");
        Romkana.put("yu", "ゆ");
        Romkana.put("yo", "よ");

        Romkana.put("wa", "わ");
        Romkana.put("wi", "うぃ");
        Romkana.put("wu", "う");
        Romkana.put("we", "うぇ");
        Romkana.put("wo", "を");

        //ya yu yo additional
        Romkana.put("pya", "ぴゃ");
        Romkana.put("pyu", "ぴゅ");
        Romkana.put("pyo", "ぴょ");

        Romkana.put("bya", "びゃ");
        Romkana.put("byu", "びゅ");
        Romkana.put("byo", "びょ");

        Romkana.put("ja", "じゃ");
        Romkana.put("ju", "じゅ");
        Romkana.put("jo", "じょ");

        Romkana.put("gya", "ぎゃ");
        Romkana.put("gyu", "ぎゅ");
        Romkana.put("gyo", "ぎょ");

        Romkana.put("rya", "りゃ");
        Romkana.put("ryu", "りゅ");
        Romkana.put("ryo", "りょ");

        Romkana.put("mya", "みゃ");
        Romkana.put("myu", "みゅ");
        Romkana.put("myo", "みょ");

        Romkana.put("hya", "ひゃ");
        Romkana.put("hyu", "ひゅ");
        Romkana.put("hyo", "ひょ");

        Romkana.put("nya", "にゃ");
        Romkana.put("nyu", "びゅ");
        Romkana.put("nyo", "にょ");

        Romkana.put("cha", "ちゃ");
        Romkana.put("chu", "ちゅ");
        Romkana.put("cho", "ちょ");

        Romkana.put("sha", "しゃ");
        Romkana.put("shu", "しゅ");
        Romkana.put("sho", "しょ");

        Romkana.put("kya", "きゃ");
        Romkana.put("kyu", "きゅ");
        Romkana.put("kyo", "きょ");

        //additionals
        Romkana.put("va", "ゔぁ");
        Romkana.put("vi", "ゔぃ");
        Romkana.put("vu", "ゔ");
        Romkana.put("ve", "ゔぇ");
        Romkana.put("vo", "ゔぉ");

        Romkana.put("fa", "ふぁ");
        Romkana.put("fi", "ふぃ");
        Romkana.put("fe", "ふぇ");
        Romkana.put("fo", "ふぉ");
        Romkana.put("dbUtil", "でゅ");
        Romkana.put("di", "でぃ");
        Romkana.put("tu", "てゅ");
        Romkana.put("ti", "てぃ");
        Romkana.put("je", "じぇ");
        Romkana.put("she", "しぇ");
        Romkana.put("che", "ちぇ");

        //n
        Romkana.put("n", "ん");



        RT = new RomajiTrie(Arrays.copyOf(Romkana.keySet().toArray(), Romkana.keySet().toArray().length, String[].class));

        htk = new HashMap<>();
        htk.put('ぁ', 'ァ');
        htk.put('あ', 'ア');
        htk.put('ぃ', 'ィ');
        htk.put('い', 'イ');
        htk.put('ぅ', 'ゥ');
        htk.put('う', 'ウ');
        htk.put('ぇ', 'ェ');
        htk.put('え', 'エ');
        htk.put('ぉ', 'ォ');
        htk.put('お', 'オ');
        htk.put('か', 'カ');
        htk.put('が', 'ガ');
        htk.put('き', 'キ');
        htk.put('ぎ', 'ギ');
        htk.put('く', 'ク');
        htk.put('ぐ', 'グ');
        htk.put('け', 'ケ');
        htk.put('げ', 'ゲ');
        htk.put('こ', 'コ');
        htk.put('ご', 'ゴ');
        htk.put('さ', 'サ');
        htk.put('ざ', 'ザ');
        htk.put('し', 'シ');
        htk.put('じ', 'ジ');
        htk.put('す', 'ス');
        htk.put('ず', 'ズ');
        htk.put('せ', 'セ');
        htk.put('ぜ', 'ゼ');
        htk.put('そ', 'ソ');
        htk.put('ぞ', 'ゾ');
        htk.put('た', 'タ');
        htk.put('だ', 'ダ');
        htk.put('ち', 'チ');
        htk.put('ぢ', 'ヂ');
        htk.put('っ', 'ッ');
        htk.put('つ', 'ツ');
        htk.put('づ', 'ヅ');
        htk.put('て', 'テ');
        htk.put('で', 'デ');
        htk.put('と', 'ト');
        htk.put('ど', 'ド');
        htk.put('な', 'ナ');
        htk.put('に', 'ニ');
        htk.put('ぬ', 'ヌ');
        htk.put('ね', 'ネ');
        htk.put('の', 'ノ');
        htk.put('は', 'ハ');
        htk.put('ば', 'バ');
        htk.put('ぱ', 'パ');
        htk.put('ひ', 'ヒ');
        htk.put('び', 'ビ');
        htk.put('ぴ', 'ピ');
        htk.put('ふ', 'フ');
        htk.put('ぶ', 'ブ');
        htk.put('ぷ', 'プ');
        htk.put('へ', 'ヘ');
        htk.put('べ', 'ベ');
        htk.put('ぺ', 'ペ');
        htk.put('ほ', 'ホ');
        htk.put('ぼ', 'ボ');
        htk.put('ぽ', 'ポ');
        htk.put('ま', 'マ');
        htk.put('み', 'ミ');
        htk.put('む', 'ム');
        htk.put('め', 'メ');
        htk.put('も', 'モ');
        htk.put('ゃ', 'ャ');
        htk.put('や', 'ヤ');
        htk.put('ゅ', 'ュ');
        htk.put('ゆ', 'ユ');
        htk.put('ょ', 'ョ');
        htk.put('よ', 'ヨ');
        htk.put('ら', 'ラ');
        htk.put('り', 'リ');
        htk.put('る', 'ル');
        htk.put('れ', 'レ');
        htk.put('ろ', 'ロ');
        htk.put('ゎ', 'ヮ');
        htk.put('わ', 'ワ');
        htk.put('ゐ', 'ヰ');
        htk.put('ゑ', 'ヱ');
        htk.put('を', 'ヲ');
        htk.put('ん', 'ン');
        htk.put('ゔ', 'ヴ');
        htk.put('ゕ', 'ヵ');
        htk.put('ゖ', 'ヶ');
    }



    public String kanify(String s) throws  InvalidRomaji{
        int computeAt = 0;
        String kana = "";
        while (computeAt != s.length()) {
            if(s.substring(computeAt).length() > 1) {
                if((s.substring(computeAt).charAt(0) == s.substring(computeAt).charAt(1))
                        && ((s.substring(computeAt).charAt(0) != 'a') &&
                        (s.substring(computeAt).charAt(0) != 'e') &&
                        (s.substring(computeAt).charAt(0) != 'i') &&
                        (s.substring(computeAt).charAt(0) != 'o') &&
                        (s.substring(computeAt).charAt(0) != 'u') &&
                        (s.substring(computeAt).charAt(0) != 'n'))) {
                    kana += "っ";
                    computeAt++;
                }
            }
            int t = RT.repeatSearch(s.substring(computeAt));
            if(t != -1 && Romkana.get(s.substring(computeAt, computeAt+t)) != null) {
                kana += Romkana.get(s.substring(computeAt, computeAt+t));
                computeAt += t;
            } else {
                throw new InvalidRomaji("Could not parse romaji input");
            }
        }
        return kana;
    }

    public String katakanify(String s) {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            out.append(htk.get(s.charAt(i)));
        }
        return out.toString();
    }
}