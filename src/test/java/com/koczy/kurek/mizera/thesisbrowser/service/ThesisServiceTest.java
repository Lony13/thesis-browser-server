package com.koczy.kurek.mizera.thesisbrowser.service;

import com.koczy.kurek.mizera.thesisbrowser.entity.Thesis;
import com.koczy.kurek.mizera.thesisbrowser.hibUtils.ThesisDAO;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisDetails;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisFilters;
import com.koczy.kurek.mizera.thesisbrowser.model.ThesisResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThesisServiceTest {
    private final int QUANTITY = 100;

    private ThesisService thesisService;
    private ThesisDAO thesisDAO;

    private List<Thesis> theses;
    private List<ThesisResponse> thesesResponse;

    @Before
    public void setUp() {
        theses = createTheses(QUANTITY);
        thesesResponse = new ArrayList<>();
        for (Thesis thesis : theses) {
            thesesResponse.add(new ThesisResponse(thesis));
        }
    }

    @Test
    public void searchTheses() {
        thesisDAO = mock(ThesisDAO.class);
        thesisService = new ThesisService(thesisDAO);

        ThesisFilters thesisFilters = new ThesisFilters();
        thesisFilters.setTitle("test");

        when(thesisDAO.searchTheses(thesisFilters)).thenReturn(theses);

        List<ThesisResponse> infos = thesisService.searchTheses(thesisFilters);

        assertEquals(QUANTITY, infos.size());

        for (int i = 0; i < infos.size(); i++) {
            assertEquals(thesesResponse.get(i).getTitle(), infos.get(i).getTitle());
        }
    }

    @Test
    public void getThesisDetails() {
        thesisDAO = mock(ThesisDAO.class);
        thesisService = new ThesisService(thesisDAO);
        ThesisDetails thesisDetails = new ThesisDetails(theses.get(0));
        int id = 1;
        when(thesisDAO.getThesis(id)).thenReturn(theses.get(0));

        ThesisDetails details = thesisService.getThesisDetails(id);

        assertEquals(thesisDetails.getTitle(), details.getTitle());
    }

    private List<Thesis> createTheses(Integer quantity) {
        List<Thesis> theses = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            theses.add(new Thesis("test" + i, "link"));
        }
        return theses;
    }
}
