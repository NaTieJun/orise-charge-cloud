package org.dromara.omind.api.common.utils;

import java.util.concurrent.atomic.AtomicLong;

public class SeqUtil {
    private Long lastTime=-1L;
    private AtomicLong addNubmer = new AtomicLong(0);
    private volatile static SeqUtil uniqueInstance = null;
    public static SeqUtil getUniqueInstance() {
        if (null == uniqueInstance) {
            synchronized (SeqUtil.class) {
                if (null == uniqueInstance) {
                    uniqueInstance = new SeqUtil();
                }
            }
        }
        return uniqueInstance;
    }
    public String getSeq()
    {
        Long currentTime=System.currentTimeMillis();
        String str;
        if((currentTime/1000) == (lastTime/1000))
        {
            str = addNubmer.toString();
            addNubmer.addAndGet(1);
            String seq = str.substring(str.length() - 4, str.length());
            return seq;
        }
        else {
            lastTime = currentTime;
            synchronized (SeqUtil.class) {
                addNubmer = new AtomicLong(lastTime + 1);
            }
            String s=currentTime.toString();
            return s.substring(s.length()-4,s.length());
        }
    }
}