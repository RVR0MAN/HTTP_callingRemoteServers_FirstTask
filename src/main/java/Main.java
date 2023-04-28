import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;



public class Main {
    public static ObjectMapper mapper = new ObjectMapper(); //служит для сериализации/десериализации

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet("https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats");
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request); //получение ответа от сервера

        List<CatsFacts> catsFacts = mapper.readValue(
                response.getEntity().getContent(), new TypeReference<List<CatsFacts>>(){}); //в параметры передаём тело ответа и экземпляр класса служащего для корректной десериализации (хранения контента)

        catsFacts.stream()
                .filter(value -> value.getUpvotes()!=null&&value.getUpvotes()>0)
                .sorted(new FactsComparator())
                .forEach(System.out::println);



        response.close();
        httpClient.close();
    }
}


class FactsComparator implements Comparator<CatsFacts>{  //для метода .sorted

    public int compare(CatsFacts a, CatsFacts b){

        return a.getUpvotes().compareTo(b.getUpvotes());
    }
}
