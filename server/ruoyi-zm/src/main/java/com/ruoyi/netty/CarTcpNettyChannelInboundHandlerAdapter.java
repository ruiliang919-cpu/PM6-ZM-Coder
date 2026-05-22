package com.ruoyi.netty;


import com.ruoyi.netty.handler.RtuHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * I/O数据读写处理类
 *
 * @author xiaobo
 */
@Slf4j
public class CarTcpNettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    private String reqMsg;
    private RtuHandler rtuHandler;

    public CarTcpNettyChannelInboundHandlerAdapter(RtuHandler rtuHandler) {
        this.rtuHandler = rtuHandler;
    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception, IOException {
        String hex = (String) msg;
        reqMsg = rtuHandler.handleReceivedData(hex);
    }

    // 从客户端收到新的数据、读取完成时调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // log.info("channelReadComplete：{}", reqMsg);
        int len = reqMsg.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(reqMsg.charAt(i), 16) << 4)
                + Character.digit(reqMsg.charAt(i + 1), 16));
        }
        ctx.writeAndFlush(Unpooled.copiedBuffer(data));
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();// 抛出异常，断开与客户端的连接
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().read();
        // InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        // String clientIp = socket.getAddress().getHostAddress();
        // 此处不能使用ctx.close()，否则客户端始终无法与服务端建立连接
        // System.out.println("channelActive:" + clientIp + ":" + ctx.name());

        // 这里是向客户端发送回应
        // ctx.writeAndFlush(Unpooled.copiedBuffer("收到over", CharsetUtil.UTF_8));
        // ctx.channel().read();
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        // String clientIp = socket.getAddress().getHostAddress();
        // 断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
        ctx.close();
        // log.info("channelInactive:{}", clientIp);
    }

    /**
     * 服务端当read超时, 会调用这个方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        // InetSocketAddress socket = (InetSocketAddress) ctx.channel().remoteAddress();
        // String clientIp = socket.getAddress().getHostAddress();
        ctx.close();// 超时时断开连接
        // log.info("userEventTriggered:{}", clientIp);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        // log.info("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        // log.info("channelUnregistered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        // log.info("channelWritabilityChanged");
    }

}
