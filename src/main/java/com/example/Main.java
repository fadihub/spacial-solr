package com.example;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



/**
* Created by Rick on 5/13/15.
*/
public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws Exception {
        test();
    }


    public static final String DOCUMENT_ID = "123456789";

    public static void test()  throws Exception {
        SolrClient client = new HttpSolrClient("http://localhost:8983/solr/todoitem");

        // Creating a doc manually.
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", DOCUMENT_ID );
        document.addField("name", "Mac computer" );
        document.addField("price", 1200 );



        //Creating 4 Todoitem objects including location of San Fran, Dublin, Sacramento, and Miami
        TodoItem item = new TodoItem("George", "finish your homework", "school", "37.7833,-122.4167");
        TodoItem  item2 = new TodoItem("John", "Buy a computer", "Shopping", "37.7022,-121.9358");
        TodoItem item3 = new TodoItem("Rose", "Go dancing", "Fun", "38.5556,-121.4689" );
        TodoItem item4 = new TodoItem("Kate", "Get nails done", "Cosmetic", "25.7753,-80.2089" );


        //Adding the document and the 2 todoitems to the solr server.
        /*Note we were able to add the items object as beans becasue we added the @Field annotation on the fields as you can
        in the TodoItem class*/

        client.add(document);
        client.addBean(item);
        client.addBean(item2);
        client.addBean(item3);
        client.addBean(item4);


        Thread.sleep(1000);


        queryAll(client);


        final SolrDocument solrDocument = lookupById(client, DOCUMENT_ID);
        System.out.printf("\nBefore updating doc %s \n %s\n", DOCUMENT_ID, solrDocument);



        updateDocument(client, solrDocument);

        Thread.sleep(1000);



        SolrDocument afterSolrDocument = lookupById(client, DOCUMENT_ID);
        System.out.printf("\nAfter updating doc %s \n %s\n", DOCUMENT_ID, afterSolrDocument);



        //To delete a document from solr then query all the docs that are on solr.
        client.deleteById(DOCUMENT_ID);

        System.out.println("\nsolr's database after deleting index with DOCUMENT_ID = 123456789:\n" );

        Thread.sleep(1000);


        queryAll(client);

        //Geo spatial search from Fremont CA
        SolrQuery query = new SolrQuery();

        query.setQuery("*:*");
        query.setStart(0);
        query.setRows(50);
        query.set("fq", "{!geofilt}");
        query.set("sfield", "location");

        query.set("pt", "37.5483,-121.9886");
        query.set("d", "300");
        query.set("wt", "json");

        QueryResponse response = client.query(query);

        System.out.println("\n geo-spatial search on solr within 300 kilometers from Fremont CA:\n" + response);

        //geo search query from Fort Lauderdale FL
        SolrQuery query1 = new SolrQuery();

        query1.setQuery("*:*");
        query1.setStart(0);
        query1.setRows(50);
        query1.set("fq", "{!geofilt}");
        query1.set("sfield", "location");

        query1.set("pt", "26.1333,-80.1500");
        query1.set("d", "100");
        query1.set("wt", "json");

        QueryResponse response1 = client.query(query1);

        System.out.println("\ngeo-spatial search on solr within 100 kilometers from Fort Lauderdale FL:\n" + response1);


        //delete all docs on solr and query the all docs on solr
//       client.deleteByQuery("*:*");
//
//
//        Thread.sleep(1000);
//
//        System.out.println("\nsolr's database after deleting everything:\n" );
//
//        queryAll(client);



    }

    private static void updateDocument(SolrClient client, SolrDocument solrDocument) throws Exception {


        SolrInputDocument inputDocument = new SolrInputDocument();

        String name = (String) solrDocument.getFirstValue("name");

        //To update the document
        Map<String,Object> nameModifier = new HashMap<>(1);
        nameModifier.put("set",  name + " with OSX ");


        Map<String,Object> priceModifier = new HashMap<>(1);
        priceModifier.put("set", "800");
        inputDocument.setField("price", priceModifier);

        inputDocument.setField("name", nameModifier);  // add the map as the field value
        inputDocument.addField("id", "123456789");
        inputDocument.addField("__version__", solrDocument.getFieldValue("__version__"));

        client.add(inputDocument);

    }


    private static SolrDocument lookupById(final SolrClient server,
                                           final String documentId) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.setQuery(documentId);
        QueryResponse response = server.query( query );
        SolrDocumentList documents = response.getResults();
        if (documents.size() == 1) {
            SolrDocument solrDocument = documents.get(0);

            return solrDocument;
        } else {
            throw new IllegalStateException("Can't find document " + documentId);
        }
    }

    private static void queryAll(SolrClient client) throws SolrServerException, IOException {
        //Query all the docs that we have posted on solr
        SolrQuery query = new SolrQuery();
        query.setQuery( "*:*" );
        QueryResponse rsp = client.query( query );
        SolrDocumentList docs = rsp.getResults();

        //for testing logger
        logger.debug("testing" + docs);

        System.out.println("Query all on solr:\n" + docs );

        /* Iterate through the document fields from query all. */
        docs.iterator().forEachRemaining(document1 -> {
            System.out.println();
            System.out.printf("field names %s\n", document1.getFieldNames());

            document1.getFieldNames().stream().forEach(fieldName -> {
                System.out.printf("Field Name %s Field Value %s \n", fieldName, document1.get(fieldName));
            });
        });
    }
}



//package com.example;
//
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.common.SolrDocumentList;
//import org.apache.solr.common.SolrInputDocument;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static java.lang.Thread.sleep;
//
///**
//* Created by fadi on 5/13/15.
//*/
//public class Main {
//
//    static final Logger logger = LoggerFactory.getLogger(Main.class);
//
//
//
//
//    public static void main(String[] args) throws Exception {
//
//
//
//        SolrClient server = new HttpSolrClient("http://192.168.59.103:8983/solr/todoitem");
//
//        // Creating a doc manually.
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("id", "123456789" );
//        document.addField("name", "Mac computer" );
//        document.addField("price", 1200 );
//
//        //Creating 2 Todoitem objects and printing them.
//        TodoItem item = new TodoItem("George", "finish your homework", "school");
//        TodoItem  item2 = new TodoItem("John", "Buy a computer", "Shopping");
//        System.out.println("Item1: \n" + item);
//        System.out.println("\nItem2: \n" +item2 +"\n");
//
//        //Adding the document and the 2 todoitems to the solr server.
//        /*Note we were able to add the items object as beans becasue we added the @Field annotation on the fields as you can
//        in the TodoItem class*/
//
//        server.add(document);
//        server.addBean(item);
//        server.addBean(item2);
//
//        server.commit();
//
//        //Query all the docs that we have posted on solr
//        SolrQuery query = new SolrQuery();
//        query.setQuery( "*:*" );
//        QueryResponse rsp = server.query( query );
//        SolrDocumentList docs = rsp.getResults();
//        System.out.println("All the docs that we have posted on solr:\n" + docs );
//
//        //Query doc id=123456789 before updating it.
//        query.setQuery( "123456789" );
//        QueryResponse rsp1 = server.query( query );
//        SolrDocumentList docs1 = rsp1.getResults();
//        System.out.println("\nBefore updating doc id=123456789:\n" + docs1);
//
//        //To update the document
//        Map<String,Object> fieldModifier = new HashMap<>(1);
//        fieldModifier.put("set", "HP pc with windows OS");
//        document.setField("name", fieldModifier);  // add the map as the field value
//
//        server.add(document);
//        server.commit();
//
//        Map<String,Object> fieldModifier1 = new HashMap<>(1);
//        fieldModifier1.put("set", "800");
//        document.setField("price", fieldModifier1);
//        server.add( document );
//        server.commit();
//        sleep(10);
//
//        //Query the doc id=123456789 after updating it
//        query.setQuery( "123456789" );
//        QueryResponse rsp2 = server.query( query );
//        SolrDocumentList docs2 = rsp2.getResults();
//
//        logger.debug("testing" + docs2);
//
//        System.out.println("\nAfter updating doc id=123456789:\n" + docs2);
//
//
//        //To delete a document from solr then query all the docs that are on solr.
//        server.deleteById("George");
//
//        server.commit();
//
//        query.setQuery( "*:*" );
//        QueryResponse rsp3 = server.query( query );
//        SolrDocumentList docs3 = rsp3.getResults();
//        System.out.println("\nAll the docs on solr after deleting id=George:\n" + docs3 );
//
//
//        //delete all docs on solr and query the all docs on solr
//        server.deleteByQuery("*:*");
//        server.commit();
//
//        QueryResponse rsp4 = server.query( query );
//        SolrDocumentList docs4 = rsp4.getResults();
//        System.out.println("\nsolr's database after deleting everything:\n" + docs4 );
//
//    }
//}