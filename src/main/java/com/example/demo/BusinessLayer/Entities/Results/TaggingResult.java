package com.example.demo.BusinessLayer.Entities.Results;

import com.example.demo.BusinessLayer.Entities.Participant;
import com.example.demo.BusinessLayer.Entities.Stages.Stage;
import com.example.demo.BusinessLayer.Entities.Stages.TaggingStage;
import com.example.demo.BusinessLayer.Exceptions.NotInReachException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.util.Pair;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tagging_results")
public class TaggingResult extends Result {
    @OneToMany(mappedBy = "taggingResult")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RequirementTag> tags;

    public TaggingResult() {
        this.tags = new ArrayList<>();
    }

    public void addTag(RequirementTag tag) {
        if (!tags.contains(tag))
            tags.add(tag);
    }

    public List<RequirementTag> getTags() {
        return tags;
    }

    public void setTags(List<RequirementTag> tags) {
        this.tags = tags;
    }

    @Override
    public void setStageAndParticipant(Stage stage, Participant participant) {
        super.setStageAndParticipant(stage, participant);
        for (RequirementTag tag : tags) {
            tag.setTaggingResult(this);
        }
    }

    @Override
    public Map<String, Object> getAsMap() {
        List<List<Map<String, Object>>> JTags = new ArrayList<>();
        String[] userCodeRows = getUserCodeByRows();
        List<Map<String, Object>> JTagsByIdx = new LinkedList<>();
        int currReqIdx = this.getTags().get(0).getRequirement().getIndex();

        for (RequirementTag tag : this.getTags()) {
            Map<String, Object> tagMapOrg =  tag.getAsMap();
            Map<String, Object> tagMapTrans = translate(tagMapOrg, userCodeRows);

            int newReqIdx = (int)tagMapOrg.get("requirement index");
            if (newReqIdx!=currReqIdx){
                currReqIdx=newReqIdx;
                JTags.add(JTagsByIdx);
                JTagsByIdx = new LinkedList<>();
            }
            else JTagsByIdx.add(tagMapTrans);

        }
        return Map.of("tags", JTags);
    }

    private Map<String, Object> translate(Map<String, Object> tagMap, String[] userCodeRows) {
        int startLoc = (int) tagMap.get("start Char"), endLoc = (int) tagMap.get("length") + startLoc;

        Pair<Integer, Integer> start = getRowCol(startLoc, userCodeRows);
        Pair<Integer, Integer> end = getRowCol(startLoc, userCodeRows);

        return Map.of("from", Map.of("row", start.getFirst(), "col", start.getSecond()),
                "to", Map.of("row", start.getFirst(), "col", 1 + start.getSecond()));

    }

    private Pair<Integer, Integer> getRowCol(int loc, String[] userCodeRows) {
        int row = 0;
        String currRow;
        for (; row < userCodeRows.length; row++) {
            currRow = userCodeRows[row];
            if (currRow.length() < loc) break;
            loc -= currRow.length();
        }
        int col = loc;
        int fRow = row + 1;
        return Pair.of(fRow, col);
    }

    private String[] getUserCodeByRows()  {
        TaggingStage t = (TaggingStage) this.getStage();
        try {
            CodeResult cr = (CodeResult) (this.getParticipant()).getResult(t.getCodeStage().getStageID().getStageIndex());
            return cr.getUserCode().split("\n");
        } catch (NotInReachException e) {
            return new String[0];
        }
    }

}
