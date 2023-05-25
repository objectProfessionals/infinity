package com.op.infinity;

public class TerrainMap extends Base {

    private static TerrainMap generate = new TerrainMap();

    //https://touchterrain.geol.iastate.edu/?trlat=51.90871366732449&trlon=-0.2405357871510594&bllat=51.47883571433833&bllon=-0.932517102763839&DEM_name=JAXA/ALOS/AW3D30/V2_2&tilewidth=100&printres=0.4&ntilesx=1&ntilesy=1&DEMresolution=191.39&basethick=1&zscale=2&fileformat=STLb&maptype=roadmap&gamma=1&transp=0&hsazi=315&hselev=45&map_lat=51.61758915672153&map_lon=-0.7970801913724479&map_zoom=10
    //https://touchterrain.geol.iastate.edu/?trlat=51.73975502353693&trlon=-0.5543943329250856&bllat=51.67209853101825&bllon=-0.6631883068478017&DEM_name=JAXA/ALOS/AW3D30/V2_2&tilewidth=100&printres=0.4&ntilesx=1&ntilesy=1&DEMresolution=30.08&basethick=1&zscale=2&fileformat=STLb&maptype=roadmap&gamma=1&transp=0&hsazi=315&hselev=45&map_lat=51.61758915672153&map_lon=-0.7970801913724479&map_zoom=10

    //51.73975502353693 -0.5543943329250856  TR
    //51.67209853101825 -0.6631883068478017 BL
    //centred on Chesham
    // max resolution @30m /px
    //nned 9
    // unitx =  -0.5543943329250856 - -0.6631883068478017 = 0.1087939739227161
    // unity =  51.73975502353693 - 51.67209853101825 = 0.06765649251868
    // trx = -0.6631883068478017 - 0.1087939739227161=-0.7719822807705178
    // try = 51.73975502353693 + 0.06765649251868=51.80741151605561
    //https://jthatch.com/Terrain2STL/
    //https://elevationapi.com/playground_3dbbox
    //.75 .50 = 184.7, 200
    public static void main(String[] args) throws Exception {
        generate.doGeneration();
    }

    private void doGeneration() {
        String url = "https://touchterrain.geol.iastate.edu/?trlat=<trlat>&trlon=<trlon>&bllat=<bllat>&bllon=<bllon>&DEM_name=JAXA/ALOS/AW3D30/V2_2&tilewidth=100&printres=0.4&ntilesx=1&ntilesy=1&DEMresolution=30.08&basethick=1&zscale=2&fileformat=STLb&maptype=roadmap&gamma=1&transp=0&hsazi=315&hselev=45&map_lat=51.61758915672153&map_lon=-0.7970801913724479&map_zoom=10";
        double ctrlaty = 51.73975502353693;
        double ctrlonx = -0.5543943329250856;
        double cbllaty = 51.67209853101825;
        double cbllonx = -0.6631883068478017;

        double unitw = cbllonx - ctrlonx;
        double unith = ctrlaty - cbllaty;

        for (double y = -1; y<2; y++) {
            for (double x = -1; x<2; x++) {
                double trlat1y = ctrlaty + y*unith;
                double trlon1x = cbllonx - (x+1)*unitw;
                double bllat1y = trlat1y - 1.2*unith;
                double bllon1x = trlon1x + 1.2*unitw;
                String url1 = url.replaceAll("<trlat>", ""+trlat1y)
                        .replaceAll("<trlon>", ""+trlon1x)
                        .replaceAll("<bllat>", ""+bllat1y)
                        .replaceAll("<bllon>", ""+bllon1x);

                System.out.println(url1);
            }
        }
    }

}