package com.yaxionh;

import com.yaxionh.parser.DongQiuDiParser;

import java.util.concurrent.CountDownLatch;

/**
 * Parsing football data
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        CountDownLatch count = new CountDownLatch(3);
        String keyword = "克洛普";
        Parser parser = new DongQiuDiParser();
        Clawer clawer = new Clawer(count, parser, keyword);
        System.out.println( "---------Start Clawering!---------" );
        clawer.clawering(parser, keyword);
        System.out.println( "---------End Clawering!---------" );
    }
}
