package com.github.cosycode.ext.hub;

import lombok.Setter;

/**
 *
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/4/2
 *
 * @author CPF
 * @since 1.2
 **/
public class LambdaProxy {

    private Runnable then;

    @Setter
    private Runnable skip;

    public LambdaProxy(Runnable then) {
        this.then = then;
    }

    public void onceExe() {
    }

    public Runnable getOnceExe() {
        return this::onceExe;
    }

    public static void main(String[] args) {

    }

}
