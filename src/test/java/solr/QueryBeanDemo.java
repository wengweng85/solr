package solr;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class QueryBeanDemo {
	
	public static final String SOLR_URL = "http://127.0.0.1:8080/solr/new_core";

	public static void main(String[] args) throws SolrServerException, IOException {
		HttpSolrClient server = new HttpSolrClient.Builder(SOLR_URL).build();
		server.setConnectionTimeout(5000); // 5 seconds to establish TCP
		SolrQuery query = new SolrQuery();
		query.setQuery("description:�ĸ�");
		query.setStart(0);
		query.setRows(2);
		query.setFacet(true);
		query.addFacetField("author_s");

		QueryResponse response = server.query(query);
		// �����õ��Ľ����
		System.out.println("Find:" + response.getResults().getNumFound());
		// ������
		int iRow = 1;
		
		//response.getBeans����BUG,��DocumentObjectBinder���õ�FieldӦ��Ϊ org.apache.solr.client.solrj.beans.Field
		SolrDocumentList list = response.getResults();
		DocumentObjectBinder binder = new DocumentObjectBinder();
		List<NewsBean> beanList=binder.getBeans(NewsBean.class, list);
		for(NewsBean news:beanList){
			System.out.println(news.getId());
		}

		for (SolrDocument doc : response.getResults()) {
			System.out.println("----------" + iRow + "------------");
			System.out.println("id: " + doc.getFieldValue("id").toString());
			System.out.println("name: " + doc.getFieldValue("name").toString());
			iRow++;
		}
		for (FacetField ff : response.getFacetFields()) {
			System.out.println(ff.getName() + "," + ff.getValueCount() + ","+ ff.getValues());
		}
	}

}
