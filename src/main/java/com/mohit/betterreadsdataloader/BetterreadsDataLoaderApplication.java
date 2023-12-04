package com.mohit.betterreadsdataloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.mohit.betterreadsdataloader.author.Author;
import com.mohit.betterreadsdataloader.author.AuthorRepository;
import com.mohit.betterreadsdataloader.book.Book;
import com.mohit.betterreadsdataloader.book.BookRepository;
import com.mohit.betterreadsdataloader.connection.DataStaxAstraProperties;

import io.netty.handler.codec.DateFormatter;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {
	
	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Value("${datadump.location.works}") 
	private String worksDumpLocation;

	@Value("${datadump.location.authors}") 
	private String authorsDumpLocation;

	
	public static void main(String[] args) {
		SpringApplication.run(BetterreadsDataLoaderApplication.class, args);
	}

	private void initialiseAuthor() {
		Path path = Paths.get(authorsDumpLocation);
		
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				//read and parse the line
				String jsonString = line.substring(line.indexOf("{"));
				JSONObject jsonObject = new JSONObject(jsonString);
				// System.out.println(jsonObject.toString());
				//construct the author object
				//System.out.println(jsonString);
				Author author = new Author();
				// author.setId(jsonObject.getString("key"));
			
				// author.setName("Mohit");
				// author.setPersonalName("Gupta");
				author.setName(jsonObject.optString("name"));
				author.setPersonalName(jsonObject.optString("personal_name"));
				author.setId(jsonObject.optString("key").replace("/authors/", ""));
				//persist using reposiory
				System.out.println("Saving " + author.getName() + "....");
				authorRepository.save(author);
			});
		} catch (IOException e) {	
			e.printStackTrace();
		}
	}

	public void initialiseWork() {
		Path path = Paths.get(worksDumpLocation);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

		try(Stream<String> lines = Files.lines(path)){
			lines.forEach(line -> {
	
				String jsonString = line.substring(line.indexOf("{"));
				JSONObject jsonObject = new JSONObject(jsonString);


				Book book = new Book();
				
				book.setId(jsonObject.optString("key").replace("/works/", ""));
	
				book.setName(jsonObject.optString("title"));

				String date = jsonObject.optJSONObject("created").optString("value");

				book.setPublishDate(LocalDate.parse(date, format));

				JSONObject descriptionObj = jsonObject.optJSONObject("desctiption");

				if(descriptionObj != null) {
					book.setDescription(descriptionObj.optString("value"));
				}
				
				JSONArray coverObj = jsonObject.optJSONArray("covers");
				if(coverObj != null) {
					
					List<String> coverIds = new ArrayList<>();
					for(int i = 0; i < coverObj.length(); i++)  {
						
						String coverId = coverObj.getInt(i) + "";

						coverIds.add(coverId);
					}
					
					book.setCoverId(coverIds);
					
				}

				
				
				JSONArray authorIdsObj = jsonObject.optJSONArray("authors");
			
				if(authorIdsObj != null) {
					
					List<String> authorIds = new ArrayList<>();
					for(int i = 0; i < authorIdsObj.length(); i++)  {
						String authorId = authorIdsObj.getJSONObject(i).optJSONObject("author").optString("key").replace("/authors/", "");
						authorIds.add(authorId);
					}
					
					book.setAuthorIds(authorIds);
					
					List<String> authorNames = authorIds.stream().map(id -> authorRepository.findById(id)).map(optionalAuthor -> {
						if(optionalAuthor.isPresent() == false) return "Unknown Author";
						else return optionalAuthor.get().getName();
					}).collect(Collectors.toList());

					book.setAuthorNames(authorNames);
				}



				
				
				bookRepository.save(book);

				
			});
		} catch(IOException e){
			System.out.println(11);
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void start() {
		initialiseAuthor();
		
		initialiseWork();
	}
	@Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
