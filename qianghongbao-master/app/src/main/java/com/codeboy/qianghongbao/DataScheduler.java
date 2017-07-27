package com.codeboy.qianghongbao;

/**
 * Created by sjb on 2017/7/24.
 */
import java.util.*;

class DataScheduler {
    private static DataScheduler instance=null;
    private List<String> id_psw;

    public static DataScheduler newInstance()
    {
        if (instance==null)
        {
            instance=new DataScheduler();
        }
        return instance;
    }

    private DataScheduler(){}

    public void init(String json)
    {

    }

    public String getOneIdPsw()
    {
        return "sujinbo8888 sjbeffort";
    }

}
