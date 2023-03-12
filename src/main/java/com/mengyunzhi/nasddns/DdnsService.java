package com.mengyunzhi.nasddns;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态域名解析
 *
 * @author panjie
 */
@Service
public class DdnsService {
  private static final Logger logger = LoggerFactory.getLogger(DdnsService.class);
  /**
   * 当前主机IP
   */
  private String currentHostIpaddress;

  /**
   * 请求次数
   * 当检测到 IP 地址发生变更时，连续请求5次 DNS 变更服务
   */
  private int requestCount = 0;

  @Autowired
  AliyunConfig aliyunConfig;

  /**
   * 获取主域名的所有解析记录列表
   */
  private DescribeDomainRecordsResponse describeDomainRecords(DescribeDomainRecordsRequest request, IAcsClient client) {
    try {
      // 调用SDK发送请求
      return client.getAcsResponse(request);
    } catch (ClientException e) {
      logger.warn("发生异常。请检查：1. AccessKey ID，AccessKey Secret是否正确。");
      logger.warn("2. 网络是否是正常");
      logger.warn("3. 是否为阿里云账户添加AliyunDNSFullAccess权限");
      e.printStackTrace();
      this.requestCount = 0;
      // 发生调用错误，抛出运行时异常
      throw new RuntimeException();
    }
  }

  /**
   * 获取当前主机公网IP
   */
  private String getCurrentHostIpaddress() {
    // 这里使用jsonip.com第三方接口获取本地IP
    String jsonip = "https://ipv4.jsonip.com/";
    // 接口返回结果
    StringBuilder result = new StringBuilder();
    BufferedReader in = null;
    try {
      // 使用HttpURLConnection网络请求第三方接口
      URL url = new URL(jsonip);
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();
      in = new BufferedReader(new InputStreamReader(
          urlConnection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result.append(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("网络错误");
      throw new RuntimeException("网络错误");
    }
    // 使用finally块来关闭输入流
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
    // 正则表达式，提取xxx.xxx.xxx.xxx，将IP地址从接口返回结果中提取出来
    String rexp = "(\\d{1,3}\\.){3}\\d{1,3}";
    Pattern pat = Pattern.compile(rexp);
    Matcher mat = pat.matcher(result.toString());
    String res = "";
    if (mat.find()) {
      res = mat.group();
    }
    return res;
  }

  /**
   * 修改解析记录
   */
  private UpdateDomainRecordResponse updateDomainRecord(UpdateDomainRecordRequest request, IAcsClient client) {
    try {
      // 调用SDK发送请求
      return client.getAcsResponse(request);
    } catch (ClientException e) {
      e.printStackTrace();
      // 发生调用错误，抛出运行时异常
      throw new RuntimeException();
    }
  }

  private static void logPrint(String functionName, Object result) {
    Gson gson = new Gson();
    logger.info("-------------------------------" + functionName + "-------------------------------");
    logger.info(gson.toJson(result));
  }

  @Scheduled(fixedRate = 60 * 1000)
  public void main() {
    // 当前主机公网IP
    logger.info("开始获取IP地址，未发生变化则有1/10的概率进行检查更新");
    String ipaddress;
    try {
      ipaddress = this.getCurrentHostIpaddress();
    } catch (RuntimeException exception) {
      this.requestCount = 0;
      return;
    }


    if (ipaddress.equals(this.currentHostIpaddress)) {
      if (this.requestCount >= 5) {
        logger.info("IP地址为：" + ipaddress + "未发生变化");
        return;
      }
    } else {
      this.requestCount = 0;
    }

    // 设置鉴权参数，初始化客户端
    DefaultProfile profile = DefaultProfile.getProfile(
        aliyunConfig.getRegion(),
        aliyunConfig.getId(),
        aliyunConfig.getSecret());
    IAcsClient client = new DefaultAcsClient(profile);

    this.aliyunConfig.getDomains().forEach(domain -> {
      // 查询指定二级域名的最新解析记录
      DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
      // 主域名
      describeDomainRecordsRequest.setDomainName(domain.getName());
      // 主机记录
      describeDomainRecordsRequest.setRRKeyWord(domain.getRecord());
      // 解析记录类型
      describeDomainRecordsRequest.setType(this.aliyunConfig.getType());
      DescribeDomainRecordsResponse describeDomainRecordsResponse = this.describeDomainRecords(describeDomainRecordsRequest, client);
      logPrint("describeDomainRecords", describeDomainRecordsResponse);

      List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();
      // 最新的一条解析记录
      if (domainRecords.size() != 0) {
        DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
        // 记录ID
        String recordId = record.getRecordId();
        // 记录值
        String recordsValue = record.getValue();

        logger.info("-------------------------------当前主机公网IP为：" + ipaddress + "-------------------------------");
        if (!ipaddress.equals(recordsValue)) {
          logger.info("更新 IP 地址");
          // 修改解析记录
          UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
          // 主机记录
          updateDomainRecordRequest.setRR(domain.getRecord());
          // 记录ID
          updateDomainRecordRequest.setRecordId(recordId);
          // 将主机记录值改为当前主机IP
          updateDomainRecordRequest.setValue(ipaddress);
          // 解析记录类型
          updateDomainRecordRequest.setType(this.aliyunConfig.getType());
          UpdateDomainRecordResponse updateDomainRecordResponse = this.updateDomainRecord(updateDomainRecordRequest, client);
          logPrint("updateDomainRecord", updateDomainRecordResponse);
        }
      }
    });

    // 未发生异常，则设置新 IP 及请求次数
    this.currentHostIpaddress = ipaddress;
    this.requestCount++;
  }
}