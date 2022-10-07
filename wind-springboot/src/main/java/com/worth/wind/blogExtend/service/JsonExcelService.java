package com.worth.wind.blogExtend.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.worth.wind.common.util.excel.ExcelExportAutoWidthStrategy;
import com.worth.wind.common.util.excel.ExcelUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 处理JSON字符串 、http请求   转换成Excel文件
 */
@Service
@Log4j2
public class JsonExcelService {


    public String jsonExcelService(String curl,String pageString,String dataIndex){
        List<LinkedHashMap<String,String>> list=getDataList(curl,1,pageString,dataIndex);
        List<String> fieldNames = new ArrayList<>();
        List<List<String>> heads = new ArrayList<>();
        for (Object o : list) {
            Map<String,Object> map= (Map<String, Object>) o;
            List<String> finalFieldNames = fieldNames;
            map.forEach((s, o1) -> finalFieldNames.add(s));
            fieldNames=finalFieldNames.stream().distinct().collect(Collectors.toList());
        }
        Collections.sort(fieldNames, String.CASE_INSENSITIVE_ORDER);
        for (String fieldName : fieldNames) {
            heads.add(new ArrayList<>(Collections.singletonList(fieldName)));
        }
        return ExcelUtils.exportBigWithBusiness("httpExcel", page -> getDataList(curl,page,pageString,dataIndex),  heads, fieldNames, new ExcelExportAutoWidthStrategy());
    }


    public List<LinkedHashMap<String,String>> getDataList(String curl,int page,String pageString,String dataIndex){
        String separator=pageString.contains("=")?"=":":";
        String pageIndex=pageString.split(separator)[0];
        curl=curl.replace(pageString,pageIndex+separator+page);

        String[] ss = curl.split("\\\\");
        List<String> value=new ArrayList<>();
        for (String s : ss) {
            value.add(s.replace("\n","").trim());
        }
        List<String> value2=new ArrayList<>();
        for (String s : value) {
            for (String s1 : s.split(" '")) {
                value2.add(s1.replace("'",""));
            }
        }
        String data= execCmdParts(value2.toArray(ss));
        log.info("==curl :{} ",String.join(" ", value2.toArray(ss)));
        log.info("==data :{} ",data);
        List<LinkedHashMap<String,String>> dataList=new ArrayList<>();
        if(StringUtils.isBlank(dataIndex)){
            dataList= JSON.parseObject(data,ArrayList.class);
        }else {
            Map<String,Object> map=JSON.parseObject(data,Map.class);
            for (int i = 0; i < dataIndex.split("\\.").length; i++) {
                String index=dataIndex.split("\\.")[i];
                if(dataIndex.split("\\.").length-1==i){
                    dataList=(map==null || map.get(index)==null)?new ArrayList<>():(List<LinkedHashMap<String,String>> )(map.get(index));
                }else {
                    map=(map==null ||map.get(index)==null)?new HashMap<>():(Map<String,Object>)map.get(index);
                }
            }
        }
        return dataList;
    }


    static String[] cmdParts1 = {"curl", "-H", "Host: www.chineseconverter.com", "-H", "Cache-Control: max-age=0", "--compressed", "https://www.chineseconverter.com/zh-cn/convert/chinese-stroke-order-tool"};
    static String[] cmdParts2 = {"curl", "-H", "Cache-Control: max-age=0", "--compressed", "https://www.chineseconverter.com/zh-cn/convert/chinese-stroke-order-tool"};
    static String[] cmdParts3 = {"curl", "-H", "Accept: application/json, text/plain, */*",
            "-H", "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
            "-H", "Authorization;",
            "-H", "Cache-Control: no-cache;",
            "-H", "Connection: keep-alive;",
            "-H", "Origin: https://www.coderfuli.com",
            "-H", "Pragma: no-cache;",
            "-H", "Referer: https://www.coderfuli.com/",
            "-H", "Sec-Fetch-Dest: empty",
            "-H", "Sec-Fetch-Mode: cors",
            "-H", "Sec-Fetch-Site: same-site",
            "-H", "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.34",
            "-H", "sec-ch-ua: \"Chromium\";v=\"106\", \"Microsoft Edge\";v=\"106\", \"Not;A=Brand\";v=\"99\"",
            "-H", "sec-ch-ua-mobile: ?0",
            "-H", "sec-ch-ua-platform: \"Windows\"",
            "--compressed", "https://api.coderfuli.com/pc/article?categoryId=&title=&orderBy=pub_time&orderDirection=desc&pageIndex=1&pageSize=10"};

    static String curl="curl 'https://api.coderfuli.com/pc/article?categoryId=&title=&orderBy=pub_time&orderDirection=desc&pageIndex=1&pageSize=10' \\\n" +
            "  -H 'Accept: application/json, text/plain, */*' \\\n" +
            "  -H 'Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6' \\\n" +
            "  -H 'Authorization;' \\\n" +
            "  -H 'Cache-Control: no-cache' \\\n" +
            "  -H 'Connection: keep-alive' \\\n" +
            "  -H 'Origin: https://www.coderfuli.com' \\\n" +
            "  -H 'Pragma: no-cache' \\\n" +
            "  -H 'Referer: https://www.coderfuli.com/' \\\n" +
            "  -H 'Sec-Fetch-Dest: empty' \\\n" +
            "  -H 'Sec-Fetch-Mode: cors' \\\n" +
            "  -H 'Sec-Fetch-Site: same-site' \\\n" +
            "  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.34' \\\n" +
            "  -H 'sec-ch-ua: \"Chromium\";v=\"106\", \"Microsoft Edge\";v=\"106\", \"Not;A=Brand\";v=\"99\"' \\\n" +
            "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
            "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
            "  --compressed";




    public static void main(String[] args) {


        // 成功
//        System.out.println(execCmdParts(cmdParts1));
        // 成功
//        System.out.println(execCmdParts(cmdParts2));
        // 失败
//        System.out.println(execCmd(String.join(" ", cmdParts1)));
        // 成功
//        System.out.println(execCmd(String.join(" ", cmdParts2)));


        String valueString="[{\"id\":\"50\",\"title\":\"#开源优选# 第6期 - Java工具库\",\"categoryId\":5,\"channel\":1,\"source\":\"\",\"sourceLink\":\"\",\"author\":{\"authorId\":\"\",\"authorName\":\"程序员福利网\"},\"summary\":\"Arthas、Guava、Fastjson、Hutool、EasyPOI、jsoup、WxJava、Sa-Token、MyBatis-Plus和Mybatis-PageHelper\",\"image\":{\"url\":\"//file.coderfuli.com/images/msDb5BQ4SKfjntTKAhpSrPkJSiRnywNC.png\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/13/50.html\",\"metric\":{\"views\":204,\"favs\":2,\"likes\":2,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2022-01-13 00:00\"},{\"id\":\"30\",\"title\":\"“意念控制”离现实还有多远？\",\"categoryId\":7,\"channel\":2,\"source\":\"虎嗅\",\"sourceLink\":\"https://www.huxiu.com/article/487259.html\",\"author\":{\"authorId\":\"\",\"authorName\":\"每经头条\"},\"summary\":\"想象一下，无需任何言语，仅靠意念就能控制外界事物，仅与同伴眼神交流就能交换想法——在关于脑机接口（Brain Computer Interfaces）技术的畅想中，这些只存在于科幻小说中的场景都将成为现实。\",\"image\":{\"url\":\"http://file.coderfuli.com/images/ypabeBsBrnAf5Q6h8tPk6ZjBijFMfKP7.jpg\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/03/30.html\",\"metric\":{\"views\":186,\"favs\":2,\"likes\":3,\"shares\":null,\"comments\":4},\"tags\":null,\"pubTime\":\"2022-01-03 17:02\"},{\"id\":\"28\",\"title\":\"李开复对谈张亚勤：科学家创业需要企业家伙伴，开放心态看待元宇宙 | MEET2022\",\"categoryId\":7,\"channel\":2,\"source\":\"36氪\",\"sourceLink\":\"https://36kr.com/p/1552296653852288\",\"author\":{\"authorId\":\"\",\"authorName\":\"量子位\"},\"summary\":\"你或许想象不到，日后为中国AI界培养了无数人才的亚洲最牛计算机研究院，23年前诞生于这样的一通“画饼”电话。\\n通话的双方，正是李开复和张亚勤。\",\"image\":{\"url\":\"http://file.coderfuli.com/images/WBTM7h8naT5DH6TNf78NfraHEGBSsPNy.jpg\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/03/28.html\",\"metric\":{\"views\":56,\"favs\":0,\"likes\":0,\"shares\":null,\"comments\":1},\"tags\":null,\"pubTime\":\"2022-01-03 00:00\"},{\"id\":\"19\",\"title\":\"Redis客户端之Jedis（Key、String、Set、List、pub/sub、cluster、pool、pipleline）\",\"categoryId\":1,\"channel\":2,\"source\":\"CSDN\",\"sourceLink\":\"https://blog.csdn.net/xyang81/article/details/51918129?spm=1001.2014.3001.5501\",\"author\":{\"authorId\":\"\",\"authorName\":\"黑鹰\"},\"summary\":\"环境：\\nOS：CentOS7 64位\\nserver版本：Redis 3.2.0\\nclient 版本：Jedis 2.8.0\",\"image\":{\"url\":\"\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/02/19.html\",\"metric\":{\"views\":86,\"favs\":0,\"likes\":3,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2022-01-02 00:00\"},{\"id\":\"25\",\"title\":\"元宇宙板块下Umee将允许DeFi速率，权益证明，流动性挖掘和NFT交叉协作\",\"categoryId\":7,\"channel\":2,\"source\":\"CSDN\",\"sourceLink\":\"https://blog.csdn.net/wangtianhong00/article/details/122279300\",\"author\":{\"authorId\":\"\",\"authorName\":\"wangtianhong00\"},\"summary\":\"Umee是一个跨链DeFi中心，在区块链之间互连。作为基础层区块链，应用程序和货币乐高原语可以构建在Umee之上，以获得跨链杠杆和流动性。Umee区块链促进了Cosmos生态系统，以太坊网络，侧链架构，第二层扩展解决方案和替代基础层协议之间的互操作性。\",\"image\":{\"url\":\"\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/03/25.html\",\"metric\":{\"views\":25,\"favs\":0,\"likes\":0,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2022-01-02 00:00\"},{\"id\":\"26\",\"title\":\"硬核，机器人只需5秒就跟踪上了爱心曲线\",\"categoryId\":7,\"channel\":2,\"source\":\"CSDN\",\"sourceLink\":\"https://blog.csdn.net/FRIGIDWINTER/article/details/122277074\",\"author\":{\"authorId\":\"\",\"authorName\":\"FrigidWinter\"},\"summary\":\"硬核，这年头机器人都开始自学“倒车入库”了这篇文章本质上属于机器人的镇定问题。本文仍基于差速轮式机器人模型完成一个轨迹跟踪应用，使机器人在尽可能短的时间内跟踪上爱心轨迹。\",\"image\":{\"url\":\"http://file.coderfuli.com/images/tk8xt2FXNMjr3XNZQKZc6Fb4XFH7ysAf.gif\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/03/26.html\",\"metric\":{\"views\":69,\"favs\":0,\"likes\":0,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2022-01-02 00:00\"},{\"id\":\"22\",\"title\":\"#开源优选# 第1期 - Java专题\",\"categoryId\":5,\"channel\":1,\"source\":\"\",\"sourceLink\":\"\",\"author\":{\"authorId\":\"\",\"authorName\":\"程序员福利网\"},\"summary\":\"Java基础、SpringBoot和SpringCloud优质学习开源项目，数据同步、数据采集和数据处理相关项目。\",\"image\":{\"url\":\"http://file.coderfuli.com/images/6friKxhkpGaSkRzsmEXkhet6CHQt588w.jpeg\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/03/22.html\",\"metric\":{\"views\":116,\"favs\":2,\"likes\":2,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2022-01-01 00:00\"},{\"id\":\"46\",\"title\":\"#开源优先# 第2期 - GO专题\",\"categoryId\":5,\"channel\":1,\"source\":\"\",\"sourceLink\":\"\",\"author\":{\"authorId\":\"\",\"authorName\":\"程序员福利网\"},\"summary\":\"静态网站生成、命令行工具、Github Actions脚本工具、文件传输、配置文件、k8s管理、云盘系统和文本命令行交互终端等项目\",\"image\":{\"url\":\"http://file.coderfuli.com/images/AZB5Rs26S5NNYnDY6C26F32TjedsAJCA.jpeg\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/05/46.html\",\"metric\":{\"views\":51,\"favs\":0,\"likes\":0,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2022-01-01 00:00\"},{\"id\":\"29\",\"title\":\"元宇宙地产愈炒愈烈，“炒房团”率先抢占元宇宙\",\"categoryId\":7,\"channel\":2,\"source\":\"36氪\",\"sourceLink\":\"https://36kr.com/p/1538085538636808\",\"author\":{\"authorId\":\"\",\"authorName\":\"神译局\"},\"summary\":\"元宇宙概念愈炒愈烈。由于这一概念刚开始兴起，相关地产行业也处于早期发展阶段。因此，许多投资者认为，现在是投资的最佳时机。他们通过在元宇宙抢占有利位置，试图打造虚拟世界中的商业版图。这篇文章来自编译，文中详细讲述了这一发展趋势背后的故事。\",\"image\":{\"url\":\"http://file.coderfuli.com/images/w8AYpSGbJnZeXjxANDtjidPkPxp6kMKf.gif\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/03/29.html\",\"metric\":{\"views\":71,\"favs\":0,\"likes\":0,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2021-12-31 00:00\"},{\"id\":\"47\",\"title\":\"#开源优先# 第3期 - Python专题\",\"categoryId\":5,\"channel\":1,\"source\":\"\",\"sourceLink\":\"\",\"author\":{\"authorId\":\"\",\"authorName\":\"程序员福利网\"},\"summary\":\"开源可视化库、Python实现的设计模式集合、Taichi编程语言、Streamlit、群组聊天软件、文件传输工具、ArchiveBox、Requests-HTML：HTML Parsing for Humans™、Zappa - Serverless Python\",\"image\":{\"url\":\"http://file.coderfuli.com/images/yE7serGGNxZefhQrfrRyaR2aDBGa7SRM.jpg\",\"width\":360,\"height\":240},\"content\":\"//file.coderfuli.com/article/2022/01/05/47.html\",\"metric\":{\"views\":63,\"favs\":0,\"likes\":0,\"shares\":null,\"comments\":0},\"tags\":null,\"pubTime\":\"2021-12-31 00:00\"}]";
        JSONArray list=JSON.parseArray(valueString);
        List<String> fieldNames = new ArrayList<>();
        List<List<String>> heads = new ArrayList<>();
        for (Object o : list) {
            Map<String,Object> map= (Map<String, Object>) o;
            List<String> finalFieldNames = fieldNames;
            map.forEach((s, o1) -> finalFieldNames.add(s));
            fieldNames=finalFieldNames.stream().distinct().collect(Collectors.toList());
        }
        for (String fieldName : fieldNames) {
            heads.add(new ArrayList<>(Collections.singletonList(fieldName)));
        }
        ExcelUtils.exportBigWithBusiness("httpExcel", page -> list,  heads, fieldNames, new ExcelExportAutoWidthStrategy());
        System.out.println();

    }
    public static String execCmdParts(String[] cmdParts) {
        ProcessBuilder process = new ProcessBuilder(cmdParts);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            return builder.toString();
        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }

        return null;
    }

    private static String execCmd(String command) {
        command=curl;
        StringBuilder output = new StringBuilder();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("line=" + line);
                output.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    /**
     * java 调用 Curl的方法
     * @param cmds
     * @return
     */
    public static String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();

        } catch (IOException e) {
            System.out.print("获取线程异常"+e);
        }
        return null;
    }
}
