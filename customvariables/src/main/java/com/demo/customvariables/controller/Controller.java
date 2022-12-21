package com.demo.customvariables.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.RuleFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.customvariables.util.Createdclass;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.codemodel.JCodeModel;

@RestController
public class Controller {

   
    @GetMapping("/getRandom")
    public Createdclass getResults(@RequestBody Map<String, Object> requestMap ) throws IOException
    { 
               
    //Writing JsonFile
    String basePathJson = "src/main/resources";
 
    File inputJson = new File(basePathJson + File.separator + "input.json");
    
    ObjectMapper mapper = new ObjectMapper();
  
    mapper.writeValue(inputJson, requestMap);


    //JsonToPojo
    
        try {
            //URL inputJsonUrl = new URL("http://localhost:8080/getRandom");
           
            String basePathPojo = "src/main/java";
            String packageName = "com.demo.customvariables.util";
            String javaClassName = "createdclass";
           
           
            File outputJavaClassDirectory = new File(basePathPojo);
            outputJavaClassDirectory.mkdirs();
            convertJsonToJavaClass( inputJson.toURI().toURL(), outputJavaClassDirectory, packageName,javaClassName );
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        //setting values
        Createdclass  createClass = new Createdclass();
        createClass = mapper.convertValue(requestMap, Createdclass.class);
        System.out.println(createClass);
        return createClass;
        
    }
    
    public void convertJsonToJavaClass(URL inputJsonUrl, File outputJavaClassDirectory, String packageName, String javaClassName) 
            throws IOException {
            
        JCodeModel jcodeModel = new JCodeModel();

              GenerationConfig config = new DefaultGenerationConfig() {
                  @Override
                  public boolean isGenerateBuilders() {
                      return true;
                  }

                  @Override
                  public SourceType getSourceType() {
                      return SourceType.JSON;
                  }
              };

              SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
              mapper.generate(jcodeModel, javaClassName, packageName, inputJsonUrl);

              jcodeModel.build(outputJavaClassDirectory);
          }
    

}
