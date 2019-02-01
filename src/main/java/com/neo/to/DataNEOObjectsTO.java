package com.neo.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
@NoArgsConstructor
public class DataNEOObjectsTO implements Serializable {
    private static final long serialVersionUID = 4670230510948686307L;
    @Setter
    @JsonProperty("links")
    private Map<String, String> links = new HashMap<>();
    @Setter
    @JsonProperty("id")
    private String id;
    @Setter
    @JsonProperty("neo_reference_id")
    private String neoReferenceId;
    @Setter
    @JsonProperty("name")
    private String name;
    @Setter
    @JsonProperty("designation")
    private String designation;
    @Setter
    @JsonProperty("nasa_jpl_url")
    private String nasaJplUrl;
    @Setter
    @JsonProperty("absolute_magnitude_h")
    private Long absMagnitude;
    @Setter
    @JsonProperty("is_potentially_hazardous_asteroid")
    private Boolean isHazardous;
    @Setter
    @JsonProperty("is_sentry_object")
    private Boolean isSentryObject;
    @Setter
    @JsonProperty("estimated_diameter")
    private Map<String, Map<String, Double>> estimatedDiameter = new HashMap<>();
    @Setter
    @JsonProperty("orbital_data")
    private Map<String, Object> orbitalData = new HashMap<>();
    @Setter
    @JsonProperty("close_approach_data")
    private List<Map<String, Object>> closeApproachData = new ArrayList<>();

}
