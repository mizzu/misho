package com.miz.misho.Utilties;

import com.miz.misho.Objects.Radical;

import java.util.ArrayList;
import java.util.Comparator;

public class RadUtil {
private ArrayList<Radical> radList;


    /**
     * Utility to store a list of radicals programatically.
     * No radical or kanji has more than 100 strokes, so this is used as a "title" placeholder for
     * any stroke count found over 100.
     */
    public RadUtil(){
    radList = new ArrayList<>();

    radList.add(new Radical("1", 101)); // placeholder for new lines
    radList.add(new Radical("ノ", 1));
    radList.add(new Radical("一", 1));
    radList.add(new Radical("｜", 1));
    radList.add(new Radical("丶", 1));
    radList.add(new Radical("亅", 1));
    radList.add(new Radical("乙", 1));
    radList.add(new Radical("2", 102)); // placeholder for new lines
    radList.add(new Radical("匕", 2));
    radList.add(new Radical("勹", 2));
    radList.add(new Radical("亠", 2));
    radList.add(new Radical("厶", 2));
    radList.add(new Radical("二", 2));
    radList.add(new Radical("ハ", 2));
    radList.add(new Radical("并", 2));
    radList.add(new Radical("入", 2));
    radList.add(new Radical("刂", 2));
    radList.add(new Radical("𠆢", 2));
    radList.add(new Radical("冂", 2));
    radList.add(new Radical("凵", 2));
    radList.add(new Radical("⻏", 2));
    radList.add(new Radical("儿", 2));
    radList.add(new Radical("冖", 2));
    radList.add(new Radical("⻖", 2));
    radList.add(new Radical("冫", 2));
    radList.add(new Radical("刀", 2));
    radList.add(new Radical("力", 2));
    radList.add(new Radical("九", 2));
    radList.add(new Radical("十", 2));
    radList.add(new Radical("又", 2));
    radList.add(new Radical("人", 2));
    radList.add(new Radical("厂", 2));
    radList.add(new Radical("几", 2));
    radList.add(new Radical("卜", 2));
    radList.add(new Radical("卩", 2));
    radList.add(new Radical("匚", 2));
    radList.add(new Radical("⺅", 2));
    radList.add(new Radical("乃", 2));
    radList.add(new Radical("マ", 2));
    radList.add(new Radical("ユ", 2));
    radList.add(new Radical("3", 103)); // placeholder for new lines
    radList.add(new Radical("ヨ", 3));
    radList.add(new Radical("川", 3));
    radList.add(new Radical("口", 3));
    radList.add(new Radical("大", 3));
    radList.add(new Radical("廾", 3));
    radList.add(new Radical("工", 3));
    radList.add(new Radical("尸", 3));
    radList.add(new Radical("巾", 3));
    radList.add(new Radical("彑", 3));
    radList.add(new Radical("忄", 3));
    radList.add(new Radical("扌", 3));
    radList.add(new Radical("⺌", 3));
    radList.add(new Radical("彳", 3));
    radList.add(new Radical("土", 3));
    radList.add(new Radical("⺾", 3));
    radList.add(new Radical("女", 3));
    radList.add(new Radical("宀", 3));
    radList.add(new Radical("夂", 3));
    radList.add(new Radical("氵", 3));
    radList.add(new Radical("犭", 3));
    radList.add(new Radical("寸", 3));
    radList.add(new Radical("山", 3));
    radList.add(new Radical("干", 3));
    radList.add(new Radical("小", 3));
    radList.add(new Radical("夕", 3));
    radList.add(new Radical("子", 3));
    radList.add(new Radical("幺", 3));
    radList.add(new Radical("屮", 3));
    radList.add(new Radical("广", 3));
    radList.add(new Radical("士", 3));
    radList.add(new Radical("弓", 3));
    radList.add(new Radical("彡", 3));
    radList.add(new Radical("久", 3));
    radList.add(new Radical("廴", 3));
    radList.add(new Radical("也", 3));
    radList.add(new Radical("亡", 3));
    radList.add(new Radical("囗", 3));
    radList.add(new Radical("弋", 3));
    radList.add(new Radical("及", 3));
    radList.add(new Radical("已", 3));
    radList.add(new Radical("尢", 3));
    radList.add(new Radical("巛", 3));
    radList.add(new Radical("4", 104)); // placeholder for new lines
    radList.add(new Radical("礻", 4));
    radList.add(new Radical("文", 4));
    radList.add(new Radical("木", 4));
    radList.add(new Radical("辶", 4));
    radList.add(new Radical("王", 4));
    radList.add(new Radical("日", 4));
    radList.add(new Radical("父", 4));
    radList.add(new Radical("水", 4));
    radList.add(new Radical("月", 4));
    radList.add(new Radical("心", 4));
    radList.add(new Radical("爪", 4));
    radList.add(new Radical("手", 4));
    radList.add(new Radical("⺹", 4));
    radList.add(new Radical("灬", 4));
    radList.add(new Radical("攵", 4));
    radList.add(new Radical("戈", 4));
    radList.add(new Radical("爿", 4));
    radList.add(new Radical("欠", 4));
    radList.add(new Radical("火", 4));
    radList.add(new Radical("牛", 4));
    radList.add(new Radical("屯", 4));
    radList.add(new Radical("毛", 4));
    radList.add(new Radical("戸", 4));
    radList.add(new Radical("氏", 4));
    radList.add(new Radical("止", 4));
    radList.add(new Radical("比", 4));
    radList.add(new Radical("斤", 4));
    radList.add(new Radical("井", 4));
    radList.add(new Radical("五", 4));
    radList.add(new Radical("曰", 4));
    radList.add(new Radical("方", 4));
    radList.add(new Radical("支", 4));
    radList.add(new Radical("犬", 4));
    radList.add(new Radical("歹", 4));
    radList.add(new Radical("毋", 4));
    radList.add(new Radical("巴", 4));
    radList.add(new Radical("勿", 4));
    radList.add(new Radical("尤", 4));
    radList.add(new Radical("无", 4));
    radList.add(new Radical("元", 4));
    radList.add(new Radical("殳", 4));
    radList.add(new Radical("爻", 4));
    radList.add(new Radical("斗", 4));
    radList.add(new Radical("片", 4));
    radList.add(new Radical("气", 4));
    radList.add(new Radical("5", 105)); // placeholder for new lines
    radList.add(new Radical("衤", 5));
    radList.add(new Radical("矢", 5));
    radList.add(new Radical("白", 5));
    radList.add(new Radical("生", 5));
    radList.add(new Radical("田", 5));
    radList.add(new Radical("示", 5));
    radList.add(new Radical("穴", 5));
    radList.add(new Radical("目", 5));
    radList.add(new Radical("禸", 5));
    radList.add(new Radical("疒", 5));
    radList.add(new Radical("禾", 5));
    radList.add(new Radical("世", 5));
    radList.add(new Radical("皿", 5));
    radList.add(new Radical("疋", 5));
    radList.add(new Radical("立", 5));
    radList.add(new Radical("用", 5));
    radList.add(new Radical("玄", 5));
    radList.add(new Radical("冊", 5));
    radList.add(new Radical("母", 5));
    radList.add(new Radical("甘", 5));
    radList.add(new Radical("癶", 5));
    radList.add(new Radical("牙", 5));
    radList.add(new Radical("矛", 5));
    radList.add(new Radical("石", 5));
    radList.add(new Radical("皮", 5));
    radList.add(new Radical("巨", 5));
    radList.add(new Radical("瓦", 5));
    radList.add(new Radical("6", 106)); // placeholder for new lines
    radList.add(new Radical("衣", 6));
    radList.add(new Radical("羊", 6));
    radList.add(new Radical("耒", 6));
    radList.add(new Radical("西", 6));
    radList.add(new Radical("耳", 6));
    radList.add(new Radical("虫", 6));
    radList.add(new Radical("而", 6));
    radList.add(new Radical("臼", 6));
    radList.add(new Radical("至", 6));
    radList.add(new Radical("自", 6));
    radList.add(new Radical("竹", 6));
    radList.add(new Radical("羽", 6));
    radList.add(new Radical("罒", 6));
    radList.add(new Radical("缶", 6));
    radList.add(new Radical("米", 6));
    radList.add(new Radical("糸", 6));
    radList.add(new Radical("聿", 6));
    radList.add(new Radical("舌", 6));
    radList.add(new Radical("舟", 6));
    radList.add(new Radical("血", 6));
    radList.add(new Radical("艮", 6));
    radList.add(new Radical("虍", 6));
    radList.add(new Radical("瓜", 6));
    radList.add(new Radical("肉", 6));
    radList.add(new Radical("行", 6));
    radList.add(new Radical("色", 6));
    radList.add(new Radical("7", 107)); // placeholder for new lines
    radList.add(new Radical("豆", 7));
    radList.add(new Radical("谷", 7));
    radList.add(new Radical("貝", 7));
    radList.add(new Radical("見", 7));
    radList.add(new Radical("臣", 7));
    radList.add(new Radical("角", 7));
    radList.add(new Radical("言", 7));
    radList.add(new Radical("邑", 7));
    radList.add(new Radical("豸", 7));
    radList.add(new Radical("足", 7));
    radList.add(new Radical("車", 7));
    radList.add(new Radical("酉", 7));
    radList.add(new Radical("辛", 7));
    radList.add(new Radical("辰", 7));
    radList.add(new Radical("里", 7));
    radList.add(new Radical("走", 7));
    radList.add(new Radical("舛", 7));
    radList.add(new Radical("豕", 7));
    radList.add(new Radical("身", 7));
    radList.add(new Radical("赤", 7));
    radList.add(new Radical("釆", 7));
    radList.add(new Radical("麦", 7));
    radList.add(new Radical("8", 108)); // placeholder for new lines
    radList.add(new Radical("斉", 8));
    radList.add(new Radical("門", 8));
    radList.add(new Radical("金", 8));
    radList.add(new Radical("長", 8));
    radList.add(new Radical("隹", 8));
    radList.add(new Radical("青", 8));
    radList.add(new Radical("隶", 8));
    radList.add(new Radical("雨", 8));
    radList.add(new Radical("免", 8));
    radList.add(new Radical("非", 8));
    radList.add(new Radical("奄", 8));
    radList.add(new Radical("岡", 8));
    radList.add(new Radical("9", 109)); // placeholder for new lines
    radList.add(new Radical("革", 9));
    radList.add(new Radical("頁", 9));
    radList.add(new Radical("風", 9));
    radList.add(new Radical("食", 9));
    radList.add(new Radical("音", 9));
    radList.add(new Radical("面", 9));
    radList.add(new Radical("品", 9));
    radList.add(new Radical("首", 9));
    radList.add(new Radical("韭", 9));
    radList.add(new Radical("香", 9));
    radList.add(new Radical("飛", 9));
    radList.add(new Radical("10", 110)); // placeholder for new lines
    radList.add(new Radical("鬼", 10));
    radList.add(new Radical("高", 10));
    radList.add(new Radical("馬", 10));
    radList.add(new Radical("骨", 10));
    radList.add(new Radical("韋", 10));
    radList.add(new Radical("鬲", 10));
    radList.add(new Radical("竜", 10));
    radList.add(new Radical("髟", 10));
    radList.add(new Radical("鬥", 10));
    radList.add(new Radical("鬯", 10));
    radList.add(new Radical("11", 111)); // placeholder for new lines
    radList.add(new Radical("鳥", 11));
    radList.add(new Radical("亀", 11));
    radList.add(new Radical("黄", 11));
    radList.add(new Radical("黒", 11));
    radList.add(new Radical("鹿", 11));
    radList.add(new Radical("麻", 11));
    radList.add(new Radical("魚", 11));
    radList.add(new Radical("滴", 11));
    radList.add(new Radical("鹵", 11));
    radList.add(new Radical("12", 112)); // placeholder for new lines
    radList.add(new Radical("無", 12));
    radList.add(new Radical("歯", 12));
    radList.add(new Radical("黍", 12));
    radList.add(new Radical("黹", 12));
    radList.add(new Radical("13", 113)); // placeholder for new lines
    radList.add(new Radical("黽", 13));
    radList.add(new Radical("鼠", 13));
    radList.add(new Radical("鼓", 13));
    radList.add(new Radical("鼎", 13));
    radList.add(new Radical("14", 114)); // placeholder for new lines
    radList.add(new Radical("齊", 14));
    radList.add(new Radical("鼻", 14));
    radList.add(new Radical("17", 117)); // placeholder for new lines
    radList.add(new Radical("龠", 17));

//    Collections.sort(radList, new radSort());


}

    public ArrayList<Radical> getRadList() {
        return radList;
    }

}
