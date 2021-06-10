/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package team;

import org.junit.Assert;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import utils.TestUtils;

import java.util.ArrayList;

public class TeamTests {

    @Test
    public void editTeamTitleAndDescriptionTest() {
        String results = TestUtils.editTeamTitleAndDescription(TestUtils.getProperty("title"), TestUtils.getProperty("description"));
        Assert.assertEquals("Team title or description was not edited", results, "Team information saved");
    }

    @Test
    @Order(2)
    public void getDownloadTeamTest() {
        String results = TestUtils.getDownloadTeamName();
        Assert.assertEquals("The GET download request was not successful", results, TestUtils.getProperty("title"));
    }

    @Test
    @Order(1)
    public void addMemberToTeamTest() {
        String response = TestUtils.addMember(TestUtils.getProperty("searchCriteria"));
        Assert.assertEquals("Team member was not added to the team", "Team member saved", response);
    }

    @Test
    @Order(3)
    public void deleteMemberFromTeamTest() {
        ArrayList<Integer> idList = TestUtils.getTeamMembersId();
        if (idList.size() == 0) {
            addMemberToTeamTest();
            idList = TestUtils.getTeamMembersId();
            String response = TestUtils.deleteTeamMember(idList.get(0).toString());
            Assert.assertEquals("The employee was not removed from the team", "Team member deleted successfully", response);
        } else {
            idList = TestUtils.getTeamMembersId();
            String response = TestUtils.deleteTeamMember(idList.get(0).toString());
            Assert.assertEquals("The employee was not removed from the team", "Team member deleted successfully", response);
        }
    }

   
}
