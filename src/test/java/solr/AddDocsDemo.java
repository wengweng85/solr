package solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

public class AddDocsDemo {
	
	
	public static final String SOLR_URL = "http://127.0.0.1:8080/solr/new_core";

	public static void main(String[] args) {
		//ͨ��������鿴���
		//http://172.168.63.233:8983/solr/collection1/select?q=name%3A%E6%94%B9%E9%9D%A9&wt=json&indent=true
		AddDocs();
		//delDocs();
	}

	public static void AddDocs() {
		String[] words = { "����ȫ����ĸ��쵼С��", "���Ĵλ���", "�����˹���н���ƶȸĸ�", "���������ƶȸĸ�",
				"��ͳý������ý���ںϵ�", "��������ļ�", "ϰ��ƽǿ��Ҫ", "�𲽹淶������ҵ�����������",
				"ʵ��н��ˮƽ�ʵ�", "�ṹ��������淶���ල��Ч", "�Բ������ƫ��", "����������е���",
				"����������ƶȸĸ�", "�ܵ�Ŀ�����γɷ��࿼��", "�ۺ�����", "��Ԫ¼ȡ�Ŀ�������ģʽ", "��ȫ�ٽ���ƽ",
				"��ѧѡ��", "�ල���������ƻ���", "��������һ����̬����", "�ֶ��Ƚ�", "���о���������������ý��",
				"���ɼ���ӵ��ǿ��ʵ���ʹ�����", "������", "Ӱ����������ý�弯��" };
		long start = System.currentTimeMillis();
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for (int i = 1; i < 1000; i++) {
			SolrInputDocument doc1 = new SolrInputDocument();
			doc1.addField("id", "id" + i, 1.0f);
			doc1.addField("name", words[i % 21], 1.0f);
			doc1.addField("price", 10 * i);
			docs.add(doc1);
		}
		try {
			HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
			// the most optimal way of updating all your docs 
			// in one http request(432ms)
			client.add(docs.iterator());
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("time elapsed(ms):"+ (System.currentTimeMillis() - start));
	}

	public static void delDocs() {
		long start = System.currentTimeMillis();
		try {
			HttpSolrClient client = new HttpSolrClient(SOLR_URL);
			List<String> ids = new ArrayList<String>();
			for (int i = 1; i < 300; i++) {
				ids.add("id" + i);
			}
			client.deleteById(ids);
			client.commit();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("time elapsed(ms):"+ (System.currentTimeMillis() - start));
	}

}
