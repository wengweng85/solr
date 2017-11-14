package solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public class AddBeansDemo {
	
	public static final String SOLR_URL = "http://127.0.0.1:8080/solr/new_core";

	public static void main(String[] args) {
		// ͨ��������鿴���
		// Ҫ��֤bean�и����Ե�������conf/schema.xml�д��ڣ������ѯ��Ҫ���汻����
		// http://172.168.63.233:8983/solr/collection1/select?q=description%3A%E6%94%B9%E9%9D%A9&wt=json&indent=true
	delBeans();
		//AddBeans();
	}

	public static Random rand = new Random(47);
	public static String[] authors = { "����", "����", "����", "����", "�ŷ�", "����",
			"���Ƴ�" };
	public static String[] links = {
			"http://repository.sonatype.org/content/sites/forge-sites/m2e/",
			"http://news.ifeng.com/a/20140818/41626965_0.shtml",
			"http://news.ifeng.com/a/20140819/41631363_0.shtml?wratingModule_1_9_1",
			"http://news.ifeng.com/topic/19382/",
			"http://news.ifeng.com/topic/19644/" };

	public static String genAuthors() {
		List<String> list = Arrays.asList(authors).subList(0, rand.nextInt(7));
		String str = "";
		for (String tmp : list) {
			str += " " + tmp;
		}
		return str;
	}

	public static List<String> genLinks() {
		return Arrays.asList(links).subList(0, rand.nextInt(5));
	}

	public static void AddBeans() {
		String[] words = { "����ȫ����ĸ��쵼С��", "���Ĵλ���", "�����˹���н���ƶȸĸ�", "���������ƶȸĸ�",
				"��ͳý������ý���ںϵ�", "��������ļ�", "ϰ��ƽǿ��Ҫ", "�𲽹淶������ҵ�����������",
				"ʵ��н��ˮƽ�ʵ�", "�ṹ��������淶���ල��Ч", "�Բ������ƫ��", "����������е���",
				"����������ƶȸĸ�", "�ܵ�Ŀ�����γɷ��࿼��", "�ۺ�����", "��Ԫ¼ȡ�Ŀ�������ģʽ", "��ȫ�ٽ���ƽ",
				"��ѧѡ��", "�ල���������ƻ���", "��������һ����̬����", "�ֶ��Ƚ�", "���о���������������ý��",
				"���ɼ���ӵ��ǿ��ʵ���ʹ�����", "������", "Ӱ����������ý�弯��" };

		long start = System.currentTimeMillis();
		Collection<NewsBean> docs = new ArrayList<NewsBean>();
		for (int i = 1; i < 300; i++) {
			NewsBean news = new NewsBean();
			news.setId("id" + i);
			news.setName("news" + i);
			news.setAuthor(genAuthors());
			news.setDescription(words[i % 21]);
			news.setRelatedLinks(genLinks());
			docs.add(news);
		}
		try {
			HttpSolrClient client =new HttpSolrClient.Builder(SOLR_URL).build();
			client.setRequestWriter(new BinaryRequestWriter());
			// ����ͨ�����ַ�ʽ����docs,����server.add(docs.iterator())Ч�����
			// ���Ӻ�ͨ��ִ��commit����commit (981ms)
			// server.addBeans(docs);
			// server.commit();

			// the most optimal way of updating all your docs
			// in one http request(481ms)
			client.addBeans(docs.iterator());
			client.optimize(); //time elasped 1176ms
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("time elapsed(ms):"+ (System.currentTimeMillis() - start));
	}

	public static void delBeans() {
		long start = System.currentTimeMillis();
		try {
			HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
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
