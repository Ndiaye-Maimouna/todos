package sn.ept.git.seminaire.cicd.cucumber.definitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import sn.ept.git.seminaire.cicd.data.TestData;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.entities.Tag;
import sn.ept.git.seminaire.cicd.repositories.TagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

@Slf4j
public class TagStepIT {

    private final static String BASE_URI = "http://localhost";
    public static final String KEY_ID = "id";
    public static final String KEY_SYSTEM_ID = "system_id";
    public static final String KEY_SYSTEM_NAME = "system_name";
    public static final String KEY_TYPE = "type";
    public static final String KEY_STATUS = "status";
    public static final String KEY_MESSAGE = "message";
    public static final String API_TAG_PATH = "/cicd/api/tags";
    public static final String KEY_NAME = "name";

    @LocalServerPort
    private int port;
    @Autowired
    private TagRepository tagRepository;

    private Response response;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //pour tags
    private String name;


    @BeforeAll
    public static void beforeAll() {
        objectMapper.findAndRegisterModules();
    }

    @Before
    public void init() {

        //pour tags
        tagRepository.deleteAll();
    }

    protected RequestSpecification request() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
        return given()
                .contentType(ContentType.JSON)
                .log()
                .all();
    }

    @Given("table tag contains data:")
    public void tableTagContainsData(DataTable dataTable) {
        List<Tag> tagsList = dataTable
                .asMaps(String.class, String.class)
                .stream()
                .map(line -> Tag
                        .builder()
                        .id(line.get(KEY_ID))
                        .name(line.get(KEY_NAME))
                        .version(0)
                        .createdDate(TestData.Default.createdDate)
                        .lastModifiedDate(TestData.Default.lastModifiedDate)
                        .build()
                ).collect(Collectors.toUnmodifiableList());
        tagRepository.saveAllAndFlush(tagsList);
    }


    @When("call find tag by id with id={string}")
    public void callFindTagByIdWithId(String id) {
        response = request()
                .when()
                .get(API_TAG_PATH + "/" + id);
    }


    @Then("the tag http status is {int}")
    public void theTagHttpStatusIs(int status) {
        response.then()
                .assertThat()
                .statusCode(status);
    }


    @Given("the returned tag has following properties:")
    public void returnedTagHasProperties(DataTable dataTable) {
        Optional<Map<String, String>> optional = dataTable
                .asMaps(String.class, String.class)
                .stream()
                .findFirst();
        Assertions.assertThat(optional).isPresent();
        Map<String, String> line = optional.get();
        response.then()
                .assertThat()
                .body(KEY_NAME, CoreMatchers.equalTo(line.get(KEY_NAME)));
    }

    @When("call find tag all with page={int}, size={int} and sort={string}")
    public void callFindAllTagWithPageSizeAndSorting(int page, int size, String sort) {
        response = request().contentType(ContentType.JSON)
                .log()
                .all()
                .when().get(API_TAG_PATH + "?page=%s&size=%s&%s".formatted(page, size, sort));
    }

    @When("call delete all tags")
    public void callDeleteAllTags() {
        response = request()
            .when()
            .delete(API_TAG_PATH);
    }


    @When("call delete tag with id={string}")
    public void callDeleteTagWithId(String id) {
        response = request()
                .when()
                .delete(API_TAG_PATH + "/" + id);
    }


    @When("call add tag")
    public void callAddTag() {
        TagDTO requestBody = TagDTO
                .builder()
                .name(this.name)
                .build();
        response = request()
                .body(requestBody)
                .when().post(API_TAG_PATH);
    }

    @When("call update tag with id={string}")
    public void callUpdateTagWithIdAndTitleAndDescription(String id) {
        TagDTO requestBody = TagDTO
                .builder()
                .name(this.name)
                .build();
        response = request()
                .body(requestBody)
                .when()
                .put(API_TAG_PATH + "/" + id);
    }


    @And("the tag returned page has following content:")
    public void theTagReturnedListHasFollowingData(DataTable dataTable) {
        List<Map<String, String>> maps = dataTable.asMaps(String.class, String.class);
        Assertions.assertThat(maps.size())
                .isEqualTo(response.jsonPath().getList("content").size());
        maps.forEach(line -> {
            response.then().assertThat()
                    .body(getTagKeyFromPageContent(KEY_NAME), Matchers.hasItem(line.get(KEY_NAME)));
        });
    }
    @And("the tag returned page has no content")
    public void theTagReturnedPageHasNoContent() {
        Assertions.assertThat(response.jsonPath().getList("content")).isEmpty();
    }

    @And("the tag returned error body looks like:")
    public void theTagErrorBodyIsLike(DataTable dataTable) {
        Optional<Map<String, String>> optional = dataTable
                .asMaps(String.class, String.class)
                .stream()
                .findFirst();
        Assertions.assertThat(optional).isPresent();
        Map<String, String> line = optional.get();
        response.then()
                .assertThat()
                .body(KEY_SYSTEM_ID, CoreMatchers.equalTo(line.get(KEY_SYSTEM_ID)))
                .body(KEY_SYSTEM_NAME, CoreMatchers.equalTo(line.get(KEY_SYSTEM_NAME)))
                .body(KEY_TYPE, CoreMatchers.equalTo(line.get(KEY_TYPE)))
                .body(KEY_STATUS, CoreMatchers.equalTo(Integer.parseInt(line.get(KEY_STATUS))))
                .body(KEY_MESSAGE, CoreMatchers.equalTo(line.get(KEY_MESSAGE)));
    }

    @And("the following tag to add:")
    public void theFollowingTagToAdd(DataTable dataTable) {
        this.theFollowingTagToUpdate(dataTable);
    }

    @And("the following tag to update:")
    public void theFollowingTagToUpdate(DataTable dataTable) {
        Optional<Map<String, String>> optional = dataTable
                .asMaps(String.class, String.class)
                .stream()
                .findFirst();
        Assertions.assertThat(optional).isPresent();
        Map<String, String> line = optional.get();
        this.name = formatTagNullable(line.get(KEY_NAME));
    }

    private String getTagKeyFromPageContent(String key) {
        return "content*.%s".formatted(Optional.ofNullable(key).orElse("?"));
    }


    private String formatTagNullable(String value) {
        return StringUtils.isNotBlank(value) && value.trim().toLowerCase().equals("null") ? null : value;
    }


}