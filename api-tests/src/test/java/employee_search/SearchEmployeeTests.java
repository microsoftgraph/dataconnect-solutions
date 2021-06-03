package employee_search;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SearchEmployeeTests {
    @Test
    public void searchSkillTest() {
        List<ArrayList> response = TestUtils.search(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        Assert.assertNotEquals("The search by skill was not succesful", 0, response.get(0).size());
    }

    @Test
    public void searchSuggestionTest() {
        String results = TestUtils.getSuggestedSkills(TestUtils.getProperty("searchSuggestion"), TestUtils.getProperty("taxonomiesURL"));
        Assert.assertTrue("No suggestions were displayed!", results.contains("suggestedSkill"));
    }
    @Test
    public void searchTaxonomyTest(){
        List<String> taxonomies;
        List<Map<String,Object>> defaultSearch = TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"), TestUtils.getProperty("taxonomies"));
        taxonomies = Arrays.asList("software","data_science","facilities","finance","human_relations","legal","sales_marketing","oilgas","healthcare");
        for(String taxonomy : taxonomies){
            List<Map<String,Object>> modifiedSearch=TestUtils.searchAndReturnProfiles(TestUtils.getProperty("searchTerms"), TestUtils.getProperty("searchCriteria"),taxonomy);
            Assert.assertNotEquals("The taxonomy was not modified", defaultSearch, modifiedSearch);
        }
    }
}
