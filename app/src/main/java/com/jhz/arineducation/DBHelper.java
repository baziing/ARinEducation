package com.jhz.arineducation;

import org.litepal.tablemanager.Connector;

public class DBHelper {

    private String[] data={
            "1,竹/竹子,bamboo-leaves",
            "1,仙人掌,cactus-big",
            "1,云/云朵,cloud-big",
            "1,珊瑚,coral-small_orange",
            "1,树叶/叶子/叶/落叶,leaf-maple-simple",
            "1,蘑菇,mushroom-toadstool",
            "2,猪/小猪/猪仔,pig",
            "1,石/石头,stone-flat",
            "1,树/树木/大树,tree",
            "1,枯木/死树,tree-dead",
            "1,树干/树枝,tree-trunk"
    };

    public void initialize(){
        Connector.getDatabase();
        for (int i=0;i<data.length;i++){
            String[] strings=data[i].split(",");
            System.out.println(strings[1]+"_______________");
//            System.out.println(strings.length);
            try {
                if (strings.length==3){
                    Model model=new Model(Integer.parseInt(strings[0]),strings[1],strings[2]);
                    model.save();
                }
            }catch (NumberFormatException e){
                System.out.println(e);
            }
        }
    }
}
