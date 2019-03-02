package com.pinKa.cloud.server;

import com.pinKa.cloud.common.FileMessage;
import com.pinKa.cloud.common.Command;
import com.pinKa.cloud.common.ReportMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;


public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ArrayList<String> serverFilesList=new ArrayList<>();
        try {
            if (msg == null) {
                return;
            }
            if (msg instanceof Command) {
                Command fr = (Command) msg;
                if (Files.exists(Paths.get("server_storage/" + fr.getCommand()))) {
                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getCommand()));
                    ctx.writeAndFlush(fm);
                }else if (fr.getCommand().equals("getReport")){
                    Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverFilesList.add(o));
                    ReportMessage rm=new ReportMessage(serverFilesList);
                    ctx.writeAndFlush(rm);
                    serverFilesList.clear();
                }else if (fr.getCommand().startsWith("delete/")){
                    String[] deleteName = fr.getCommand().split("/");
                    Files.deleteIfExists(Paths.get("server_storage/"+deleteName[1]));
                    Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverFilesList.add(o));
                    ReportMessage rm=new ReportMessage(serverFilesList);
                    ctx.writeAndFlush(rm);
                    serverFilesList.clear();
                }
            }
            if (msg instanceof FileMessage){
                FileMessage fm=(FileMessage)msg;
                Files.write(Paths.get("server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                Files.list(Paths.get("server_storage")).map(p -> p.getFileName().toString()).forEach(o -> serverFilesList.add(o));
                ReportMessage rm=new ReportMessage(serverFilesList);
                ctx.writeAndFlush(rm);
                serverFilesList.clear();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
