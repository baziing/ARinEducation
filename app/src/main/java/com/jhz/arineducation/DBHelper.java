package com.jhz.arineducation;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class DBHelper {

    private String[] data={
            "1,竹/竹子,bamboo-leaves,zhu4/zhu4.zi5,bamboo",
            "1,仙人掌,cactus-big,xian1.ren2.zhang3,cactus",
            "1,云/云朵,cloud-big,yun2/yun2.duo3,cloud",
            "1,珊瑚,coral-small_orange,shan1.hu2,coral",
            "1,树叶/叶子/叶/落叶,leaf-maple-simple,shu4.ye4/ye4.zi5/ye4/luo4.ye4,leaf",
            "1,蘑菇,mushroom-toadstool,mo2.gu5,mushroom",
            "2,猪/小猪/猪仔,pig,zhu1/xiao3.zhu1/zhu1zai3,pig",
            "1,石/石头,stone-flat,shi2/shi2.tou5,stone",
            "1,树/树木/大树,tree,shu4/shu4.mu4/da4.shu4,tree",
            "1,枯木/死树,tree-dead,ku1.mu4/si3.shu4,deadwood",
            "1,树干/树枝,tree-trunk,shu4.gan4/shu4.zhi1,trunk"
    };

    public void initialize(){
        Connector.getDatabase();
        for (int i=0;i<data.length;i++){
            String[] strings=data[i].split(",");
            System.out.println(strings[1]+"_______________");
//            System.out.println(strings.length);
            try {
                if (strings.length==5){
                    Model model=new Model(Integer.parseInt(strings[0]),strings[1],strings[2],strings[3],strings[4]);
                    System.out.println(strings[2]+"++++++++++++++++");
                    model.save();
                }
            }catch (NumberFormatException e){
                System.out.println(e);
            }
        }
    }

    public String findObjectByChi(String str){
        List<Model>models= LitePal.where("chi like ?","%"+str+"%").find(Model.class);
        for (Model model:models){
            return model.getModelName();
        }
        return null;
    }

    public String findObjectByEng(String str){
        str=str.toLowerCase();
        List<Model>models= LitePal.where("eng like ?","%"+str+"%").find(Model.class);
        for (Model model:models){
            return model.getModelName();
        }
        return null;
    }

    public String findPinyin(String str){
        List<Model>models= LitePal.where("chi like ?","%"+str+"%").find(Model.class);
        for (Model model:models){
            return model.getPinyin();
        }
        return null;
    }

    public String findCharacters(String str){
        List<Model>models= LitePal.where("chi like ?","%"+str+"%").find(Model.class);
        for (Model model:models){
            return model.getChi();
        }
        return null;
    }
}
