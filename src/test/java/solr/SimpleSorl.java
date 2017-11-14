package solr;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class SimpleSorl {
    private String solrUrl;
    private HttpSolrClient  client;
    private int num = 10;

    /**
     * 创建solr client服务
     * @return
     */
    private HttpSolrClient createNewSolrClient() {
        try {
            System.out.println("server address:" + solrUrl);
            client =new HttpSolrClient.Builder(solrUrl).build();
            client.setConnectionTimeout(30000);
            client.setDefaultMaxConnectionsPerHost(100);
            client.setMaxTotalConnections(100);
            client.setSoTimeout(30000);
            return client;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 关闭服务
     */
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SimpleSorl(String solrUrl, int num) {
        this.solrUrl = solrUrl;
        this.client = createNewSolrClient();
        this.num = num;
    }


   /**
    * 创建文档
    */
    public void createDocs() {
        System.out.println("======================add doc ===================");
        Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        for (int i = 1; i <= 10000; i++) {
            SolrInputDocument doc1 = new SolrInputDocument();
            doc1.addField("id", UUID.randomUUID().toString(), 1.0f);
            doc1.addField("name", "bean");
            doc1.addField("equIP_s", "192.168.2.104");
            doc1.addField("level_s", "4");
            doc1.addField("collectPro_s", "ffffffffffffffffffffjajajajajajdddddddddd");
            doc1.addField("sourceType_s", "miaohqaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            doc1.addField("filePath_s", "/home/xxxx/test");
            doc1.addField("filename_s", "zhonggggmaiaiadadadddddddddddddddddddddddddd");//            doc1.addField("_route_", "shard1");
            docs.add(doc1);
        }
        try {
            UpdateResponse rsp = client.add(docs.iterator());
            System.out .println("Add doc size" + docs.size() + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
            UpdateResponse rspcommit = client.commit();
            System.out.println("commit doc to index" + " result:" + rspcommit.getStatus() + " Qtime:" + rsp.getQTime());
        } catch (SolrServerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 查询文档
     */
    public void queryDocs() {
        SolrQuery query = new SolrQuery();
        query.setQuery("f_title_ik:管理");
        query.setFields("f_title_ik","f_value_ik");
        query.setStart(0);
		query.setRows(20);
        query.setHighlight(true);
        //query.setHighlightSimplePre("<font color='red'>");
        //query.setHighlightSimplePost("</font>");
        try {
            QueryResponse rsp = client.query(query);
            SolrDocumentList docs = rsp.getResults();
            System.out.println("查询内容:" + query);
            System.out.println("文档数量：" + docs.getNumFound());
            System.out.println("查询花费时间:" + rsp.getQTime());
            System.out.println("------query data:------");
            for (SolrDocument doc : docs) {
                // 多值查询
                String f_title_ik =(String)doc.getFieldValue("f_title_ik");
                String f_value_ik = (String) doc.getFieldValue("f_value_ik");
                System.out.println("f_title_ik:" + f_title_ik + "\t f_value_ik:" + f_value_ik);
            }
            System.out.println("-----------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 删除文档
     * @param id
     */
    public void deleteById(String id) {
        System.out.println("======================deleteById ===================");
        try {
            UpdateResponse rsp = client.deleteById(id);
            client.commit();
            System.out.println("delete id:" + id + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文档
     * @param queryCon
     */
    public void deleteByQuery(String queryCon) {
        System.out.println("======================deleteByQuery ===================");
        UpdateResponse rsp;
        try {
            UpdateRequest commit = new UpdateRequest();
            commit.deleteByQuery(queryCon);
            commit.setCommitWithin(5000);
            commit.process(client);
            System.out.println("url:"+commit.getPath()+"\t xml:"+commit.getXML()+" method:"+commit.getMethod());
//            rsp = client.deleteByQuery(queryCon);
//            client.commit();
//            System.out.println("delete query:" + queryCon + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
        } catch (SolrServerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 更新字段
     * @param id
     * @param fieldName
     * @param fieldValue
     */
    public void updateField(String id,String fieldName, Object fieldValue) {
        System.out.println("======================updateField ===================");
        HashMap<String, Object> oper = new HashMap<String, Object>();
//        多值更新方法
//        List<String> mulitValues = new ArrayList<String>();
//        mulitValues.add(fieldName);
//        mulitValues.add((String)fieldValue);
        oper.put("set", fieldValue);

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", id);
        doc.addField(fieldName, oper);
        try {
            UpdateResponse rsp = client.add(doc);
            System.out.println("update doc id" + id + " result:" + rsp.getStatus() + " Qtime:" + rsp.getQTime());
            UpdateResponse rspCommit = client.commit();
            System.out.println("commit doc to index" + " result:" + rspCommit.getStatus() + " Qtime:" + rspCommit.getQTime());

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        String url = "http://127.0.0.1:8080/solr/new_core";
        SimpleSorl ss = new SimpleSorl(url, 2);
        ss.queryDocs();
        // 添加文档
        //ss.createDocs();

     /*   // 删除文档
        ss.deleteByQuery("name:bean");
        
        //更新文档
        ss.updateField("bd67564f-4939-4de1-9a83-3483ebbbbbee", "name", "1233313131313");
        
        ss.close();

        // 查询文档
        ss.queryDocs();
        ss.close();*/

    }

}