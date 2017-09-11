package io.xx;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

public class SearchMain { 

    public static void main(String[] args) {
        
        String match = ".*";

        if (args == null || args.length == 0) {
            System.out.println("params: search root folder");
            System.out.println("params[default=.*]: file filter regex");
        } else if (args.length >= 2) {
            match = args[1];
        }

        File pFd = new File(args[0]);

        searchFd(pFd, match);
    }

    public static boolean searchFd (File pFd, final String regex) {
        
        if (pFd.isFile()) {
            
            searchF(pFd);
            return true;
        }


        for (File f : pFd.listFiles(new FileFilter(){
            
                        public boolean accept(File f) {
            
                            if (!f.isDirectory() && f.getName().matches(regex)) {
                                return true;
                            }
                            return false;
                        }
                    })) {
                        searchF(f);
                    }



        for (File f : pFd.listFiles(new FileFilter(){

            public boolean accept(File f) {

                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    return true;
                }
                return false;
            }
        })) {
            searchFd(f, regex);
        }

        return true;
    }

    public static boolean searchF(File f) {

        byte lf = 10;
        byte cr = 13;

        try (FileInputStream fis = new FileInputStream(f)){
            System.out.print(f.getCanonicalPath());
            // BufferedReader bdr = new BufferedReader(new FileReader(f));
            if (f.length() >= (1024 * 1024)) {
                System.out.println("File size >= 1M, skiped");
                return true;
            }
            
            byte[] data = new byte[(int)f.length() + 1];
            fis.read(data);
            data[data.length - 1] = 'T';

            StringBuilder sb = new StringBuilder();
            int flg = 0x00;
            for (int i = 0; i < data.length - 1; i++) {
                
                // judge binary file
                // if (data[i] > 127 && data[i] < 160 ||
                //    data[i] >= 160 && data[i + 1] < 160 ||
                //    data[i] < 32 && (data[i] != 9 && data[i] != 10)) {
                //        sb.append(" : binary file! skiped");
                //        break;
                //    }

                if (data[i] == cr && data[i + 1] == lf) {
                    flg = (flg | 0x04);
                    i++;
                } else if (data[i] == lf && data[i + 1] == cr) {
                    flg = flg | 0x08;
                    i++;
                } else if (data[i] == lf) {
                    flg = flg | 0x02;
                } else if (data[i] == cr) {
                    flg = flg | 0x01;
                }
            }

            if ((flg & 0x08) > 0) {
                sb.append(" LFCR ");
            } else sb.append("      ");

            if ((flg & 0x04) > 0) {
                sb.append(" CRLF ");
            } else sb.append("      ");

            if ((flg & 0x02) > 0) {
                sb.append( " LF " );
            } else sb.append("    ");

            if ((flg & 0x01) > 0) {
                sb.append(" CR ");
            } else sb.append("    ");

            System.out.print(sb.toString() + " [END]");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
        return true;
    }
}
