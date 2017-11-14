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
		//��������£����²�����������
		//ʹ���ϰ汾solrj�����°汾��solrʱ����Ϊ�����汾��javabin incompatible,������Ҫ����Parser
		client.setParser(new XMLResponseParser());
		client.setSoTimeout(1000); // socket read timeout
		client.setDefaultMaxConnectionsPerHost(100);
		client.setMaxTotalConnections(100);
		client.setFollowRedirects(false); // defaults to false
		// allowCompression defaults to false.
		// Server side must support gzip or deflate for this to have any effect.
		client.setAllowCompression(true);

		//ʹ��ModifiableSolrParams���ݲ���
//		ModifiableSolrParams params = new ModifiableSolrParams();
//		// 192.168.230.128:8983/solr/select?q=video&fl=id,name,price&sort=price asc&start=0&rows=2&wt=json
//		// ���ò�����ʵ������URL�еĲ�������
//		// ��ѯ�ؼ���
//		params.set("q", "video");
//		// ������Ϣ
//		params.set("fl", "id,name,price,score");
//		// ����
//		params.set("sort", "price asc");
//		// ��ҳ,start=0���Ǵ�0��ʼ,rows=5��ǰ����5����¼,�ڶ�ҳ���Ǳ仯start���ֵΪ5�Ϳ�����
//		params.set("start", 2);
//		params.set("rows", 2);
//		// ���ظ�ʽ
//		params.set("wt", "javabin");
//		QueryResponse response = server.query(params);

		//ʹ��SolrQuery���ݲ�����SolrQuery�ķ�װ�Ը���
		client.setRequestWriter(new BinaryRequestWriter());
		SolrQuery query = new SolrQuery();
		query.setQuery("����");
		query.setFields("id","name","price","score");
		query.setSort("price", ORDER.asc);
		query.setStart(0);
		query.setRows(2);
//		query.setRequestHandler("/select");
		QueryResponse response = client.query( query );
		
		// �����õ��Ľ����
		System.out.println("Find:" + response.getResults().getNumFound());
		// ������
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
