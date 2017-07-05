package com.nova.steps;

import com.nova.pages.*;

/**
 * Created by Saleem on 18/06/2017.
 */
public class BaseSteps {


    protected AdvancedSearch advancedSearch;
    protected Home home;
    protected Site site;
    protected SearchResults searchResults;
    protected CartContents cartContents;
    protected Buy buy;
    protected Treasury treasury;



    public BaseSteps(PageFactory pageFactory){

        advancedSearch = pageFactory.newAdvancedSearch();
        home = pageFactory.newHome();
        site = pageFactory.newSite();
        searchResults = pageFactory.newSearchResults();
        cartContents = pageFactory.newCartContents();
        buy = pageFactory.newBuy();
        treasury = pageFactory.newTreasury();
    }
}
