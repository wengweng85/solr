package solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

public class QueryDocsDemo {
	
	public static final String SOLR_URL = "http://127.0.0.1:8080/solr/new_core";

	public static void main(String[] args) throws SolrServerException, IOException {
		HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
		client.setConnectionTimeout(5000); // 5 seconds to establish TCP
		//正常情况下，以下参数无须设置
		//使用老版本solrj操作新版本的solr时，因为两个版本的javabin incompatible,所以需要设置Parser
		client.setParser(new XMLResponseParser());
		client.setSoTimeout(1000); // socket read timeout
		client.setDefaultMaxConnectionsPerHost(100);
		client.setMaxTotalConnections(100);
		client.setFollowRedirects(false); // defaults to false
		// allowCompression defaults to false.
		// Server side must support gzip or deflate for this to have any effect.
		client.setAllowCompression(true);

		//使用ModifiableSolrParams传递参数
//		ModifiableSolrParams params = new ModifiableSolrParams();
//		// 192.168.230.128:8983/solr/select?q=video&fl=id,name,price&sort=price asc&start=0&rows=2&wt=json
//		// 设置参数，实现上面URL中的参数配置
//		// 查询关键词
//		params.set("q", "video");
//		// 返回信息
//		params.set("fl", "id,name,price,score");
//		// 排序
//		params.set("sort", "price asc");
//		// 分页,start=0就是从0开始,rows=5当前返回5条记录,第二页就是变化start这个值为5就可以了
//		params.set("start", 2);
//		params.set("rows", 2);
//		// 返回格式
//		params.set("wt", "javabin");
//		QueryResponse response = server.query(params);

		//使用SolrQuery传递参数，SolrQuery的封装性更好
		client.setRequestWriter(new BinaryRequestWriter());
		SolrQuery query = new SolrQuery();
		query.setQuery("管理");
		query.setFields("id","name","price","score");
		query.setSort("price", ORDER.asc);
		query.setStart(0);
		query.setRows(2);
//		query.setRequestHandler("/select");
		QueryResponse response = client.query( query );
		
		// 搜索得到的结果数
		System.out.println("Find:" + response.getResults().getNumFound());
		// 输出结果
		int iRow = 1;
		for (SolrDocument doc : response.getResults()) {
			System.out.println("----------" + iRow + "------------");
			System.out.println("id: " + doc.getFieldValue("id").toString());
			System.out.println("name: " + doc.getFieldValue("name").toString());
			System.out.println("price: " + doc.getFieldValue("price").toString());
			System.out.println("score: " + doc.getFieldValue("score"));
			iRow++;
		}
	}

}
