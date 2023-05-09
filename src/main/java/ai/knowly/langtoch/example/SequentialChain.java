package ai.knowly.langtoch.example;

import ai.knowly.langtoch.capability.graph.CapabilityGraph;
import ai.knowly.langtoch.capability.graph.NodeAdapter;
import ai.knowly.langtoch.capability.module.openai.unit.SimpleTextCapabilityUnit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;

public class SequentialChain {
  public static void main(String[] args) {
    SimpleTextCapabilityUnit unit = SimpleTextCapabilityUnit.create();

    // Graph:
    // A: Generate company name based on the business description
    // B: Generate Slogan based on the company name
    CompanyNameGenerator companyNameGenerator =
        CompanyNameGenerator.create(unit, "A", ImmutableList.of("B"));
    SloganGenerator sloganGenerator = SloganGenerator.create(unit, "B", ImmutableList.of());
    // Business Description -> A -> B -> Output
    CapabilityGraph capabilityGraph = CapabilityGraph.create();
    capabilityGraph.addNode(companyNameGenerator, String.class);
    capabilityGraph.addNode(sloganGenerator, String.class);
    Map<String, Object> result =
        capabilityGraph.process(ImmutableMap.of("A", "Search engine solution provider"));
    String slogan = (String) result.get("B");
    System.out.println(slogan);
  }

  private static class CompanyNameGenerator implements NodeAdapter<String, String> {
    private final SimpleTextCapabilityUnit unit;
    private final String id;
    private final List<String> outDegree;

    private CompanyNameGenerator(SimpleTextCapabilityUnit unit, String id, List<String> outDegree) {
      this.unit = unit;
      this.id = id;
      this.outDegree = outDegree;
    }

    public static CompanyNameGenerator create(
        SimpleTextCapabilityUnit unit, String id, List<String> outDegree) {
      return new CompanyNameGenerator(unit, id, ImmutableList.copyOf(outDegree));
    }

    @Override
    public String getId() {
      return this.id;
    }

    @Override
    public List<String> getOutDegree() {
      return this.outDegree;
    }

    @Override
    public String process(Iterable<String> inputs) {
      return this.unit.run(
          "Please provide a creative company name based on the business description: "
              + ImmutableList.copyOf(inputs).get(0));
    }
  }

  private static class SloganGenerator implements NodeAdapter<String, String> {
    private final SimpleTextCapabilityUnit unit;
    private final String id;
    private final List<String> outDegree;

    private SloganGenerator(SimpleTextCapabilityUnit unit, String id, List<String> outDegree) {
      this.unit = unit;
      this.id = id;
      this.outDegree = outDegree;
    }

    public static SloganGenerator create(
        SimpleTextCapabilityUnit unit, String id, List<String> outDegree) {
      return new SloganGenerator(unit, id, ImmutableList.copyOf(outDegree));
    }

    @Override
    public String getId() {
      return this.id;
    }

    @Override
    public List<String> getOutDegree() {
      return this.outDegree;
    }

    @Override
    public String process(Iterable<String> inputs) {
      return this.unit.run(
          "Please provide a creative slogan based on the company name: "
              + ImmutableList.copyOf(inputs).get(0));
    }
  }
}