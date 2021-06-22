<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { Line, mixins } from 'vue-chartjs';
const { reactiveProp } = mixins;

@Component({
  components: {},
  extends: Line,
  mixins: [reactiveProp],
  methods: {}
})
export default class LineChart extends Vue {
  @Prop({
    default: () => {
      return {};
    }
  })
  chartData!: any;

  @Prop({
    default: () => {
      return {};
    }
  })
  options!: any;
  public renderChart!: (chartData: any, options: any) => void;

  mounted() {
    // this.chartData is created in the mixin.
    // If you want to pass options please create a local options object
    this.renderChart(this.chartData, this.options);
  }
}
</script>

<style lang="scss"></style>
