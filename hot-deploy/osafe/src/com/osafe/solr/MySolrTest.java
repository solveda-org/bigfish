package com.osafe.solr;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;

public class MySolrTest {

    public static void main(String[] args) throws MalformedURLException, SolrServerException {

        // Props systemProps = Props.getSystemProps();
        // "http://localhost:8983/solr/mbartists"
        // String solrServer = "http://localhost:8983/solr";
        String solrServer = "http://localhost:8080/solr";
        CommonsHttpSolrServer solr = new CommonsHttpSolrServer(solrServer);
        solr.setRequestWriter(new BinaryRequestWriter());

        // Document Results
        System.out.println("All Results - no criteria");

        String query = "*:*";
        SolrQuery solrQuery = new SolrQuery(query);

        QueryResponse response = solr.query(solrQuery);
        SolrDocumentList results = response.getResults();
        int index = 0;
        System.out.println("0 - " + results.size() + " of " + results.getNumFound());
        while (index < results.size()) {
            SolrDocument solrDocument = results.get(index);
            System.out.print("" + (index + 1));
            System.out.print("\t");
            System.out.print(solrDocument.get("id"));
            System.out.print("\t");
            System.out.print(solrDocument.get("name"));
            System.out.print("\t");
            System.out.print(solrDocument.get("price"));
            System.out.print("\n");
            index++;
        }

        // Pricing Facet Results
        System.out.println("");
        System.out.println("Pricing Facets for 'All Results'");
        String queryFacetPricing = "*:*";
        SolrQuery solrQueryFacetPricing = new SolrQuery(queryFacetPricing);
        solrQueryFacetPricing.setFacet(true);

        solrQueryFacetPricing.addFacetQuery("price:[* 100]");
        solrQueryFacetPricing.addFacetQuery("price:[100 200]");
        solrQueryFacetPricing.addFacetQuery("price:[200 300]");
        solrQueryFacetPricing.addFacetQuery("price:[300 *]");
        QueryResponse responseFacetPricing = solr.query(solrQueryFacetPricing);
        Map<String, Integer> resultsFacetPricing = responseFacetPricing.getFacetQuery();

        Iterator<String> iteratorFacetPricing = resultsFacetPricing.keySet().iterator();
        System.out.println("Facets By:'" + "Price Range" + "'");
        while (iteratorFacetPricing.hasNext()) {
            String key = iteratorFacetPricing.next();
            Integer numberInRange = resultsFacetPricing.get(key);

            System.out.print(key);
            System.out.print("\t");
            System.out.print(numberInRange);
            System.out.print("\n");
        }

        // Facet Results
        System.out.println("");
        System.out.println("Facets for 'All Results'");
        String queryFacet = "*:*";
        SolrQuery solrQueryFacet = new SolrQuery(queryFacet);
        solrQueryFacet.setFacet(true);
        solrQueryFacet.addFacetField("cat");
        solrQueryFacet.addFacetField("inStock");

        solrQueryFacet.setFacetSort(FacetParams.FACET_SORT_COUNT);
        // solrQueryFacet.setFacetMinCount(1);
        // solrQueryFacet.setFacetLimit(3);

        QueryResponse responseFacet = solr.query(solrQueryFacet);

        // This section could be converted to use
        // List<[OurOwnClass]> beans = response.getBeans([OurOwnClass].class);
        // instead of the generic classes seen below
        List<FacetField> resultsFacet = responseFacet.getFacetFields();

        index = 0;
        while (index < resultsFacet.size()) {
            FacetField facetField = resultsFacet.get(index);

            System.out.println("Facets By:'" + facetField.getName() + "'");

            List<Count> values = facetField.getValues();

            // Need to create a delegator
//            Comparator<Count> countCompare = new FacetValueSequenceComparator();
//            Collections.sort(values, countCompare);
            for (Count count : values) {
                System.out.print(count.getName());
                System.out.print("\t");
                System.out.print(count.getCount());
                System.out.print("\n");
            }
            System.out.print("\n");
            index++;
        }

        // Document Results by clicking on a facet
        System.out.println("");
        System.out.println("Results - for a single facet");
        query = "cat:memory";
        System.out.println("query ='" + query + "'");

        solrQuery = new SolrQuery(query);

        response = solr.query(solrQuery);

        // This section could be converted to use
        // List<[OurOwnClass]> beans = response.getBeans([OurOwnClass].class);
        // instead of the generic classes seen below
        results = response.getResults();
        index = 0;
        System.out.println("0 - " + results.size() + " of " + results.getNumFound());
        while (index < results.size()) {
            SolrDocument solrDocument = results.get(index);
            System.out.print("" + (index + 1));
            System.out.print("\t");
            System.out.print(solrDocument.get("id"));
            System.out.print("\t");
            System.out.print(solrDocument.get("name"));
            System.out.print("\n");
            System.out.print(solrDocument.get("price"));
            System.out.print("\n");
            index++;
        }

    }
}
