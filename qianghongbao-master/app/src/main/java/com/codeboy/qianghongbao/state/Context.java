package com.codeboy.qianghongbao.state;

/**
 * Created by sjb on 2017/7/26.
 */

public class Context {

    private State state=null;

    public void SetState(State state)
    {
        this.state=state;
        this.state.doJob();
    }
}
