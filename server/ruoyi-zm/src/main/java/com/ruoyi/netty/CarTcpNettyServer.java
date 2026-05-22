package com.ruoyi.netty;

import com.ruoyi.netty.handler.RtuHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;

/**
 * description: <h1>netty创建的TCP</h1>
 *
 * @author bo
 * @version 1.0
 * @date 2024/2/27 16:25
 */
@Slf4j
// @Component
// @Lazy
public class CarTcpNettyServer implements CommandLineRunner {

    @Resource
    private RtuHandler rtuHandler;

    public void bind(int port) throws Exception {

        // 配置服务端的NIO线程组
        // NioEventLoopGroup 是用来处理I/O操作的Reactor线程组
        // bossGroup：用来接收进来的连接，workerGroup：用来处理已经被接收的连接,进行socketChannel的网络读写，
        // bossGroup接收到连接后就会把连接信息注册到workerGroup
        // workerGroup的EventLoopGroup默认的线程数是CPU核数的二倍

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // netty 默认数据包传输大小为1024字节, 设置它可以自动调整下一次缓冲区建立时分配的空间大小，避免内存的浪费    最小  初始化  最大 (根据生产环境实际情况来定)
                // 使用对象池，重用缓冲区
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576))
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 10496, 1048576))
                // 设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
                .childHandler(new CarTcpNettyChannelInitializer<SocketChannel>(rtuHandler));
            log.info("<===========netty server start success!==============>");
            // 绑定端口，同步等待成功
            ChannelFuture f = serverBootstrap.bind(port).sync();
            // 等待服务器监听端口关闭
            f.channel().closeFuture().sync();

        } finally {
            // 退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public void run(String... args) throws Exception {
        this.bind(1503);
    }
}
