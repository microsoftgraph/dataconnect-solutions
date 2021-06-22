import {
  Action,
  getModule,
  Module,
  Mutation,
  VuexModule
} from 'vuex-module-decorators';
import store from '@/store';
import moment from 'moment';
import axios from 'axios';
import { chartColors } from '@/shared/helpers';
import { GroupsStore } from './groups.store';

export interface CalendarTimeInterval {
  start: moment.Moment;
  end: moment.Moment;
}
export interface MetricsResponse {
  participationStatusCountsResponse: {
    counts: number;
    day: string;
    participationStatus: string;
  }[];
  selectedStatusCounts: {
    counts: number;
    day: string;
    selectedStatus: string;
  }[];
  averageGroupCountsPerDayInIntervalResponse: [
    {
      avgGroupCounts: number;
    }
  ];
  averageParticipationPerGroupPerDay: [
    {
      averageParticipationIngroup: number;
    }
  ];
  avgMinMaxGroupSizeResponse: [
    { avgGroupSize: number; maxGroupSize: number; minGroupSize: number }
  ];
  weekGroupLoadResponse: {
    fri: {
      [key: number]: number;
    };
    mon: {
      [key: number]: number;
    };
    thu: {
      [key: number]: number;
    };
    tue: {
      [key: number]: number;
    };
    wed: {
      [key: number]: number;
    };
  };
}
@Module({
  dynamic: true,
  store: store,
  name: 'Metrics',
  namespaced: true
})
class MetricsStoreModule extends VuexModule {
  public rawChartData: MetricsResponse = {} as MetricsResponse;
  public chartData = {
    Heatmap: {
      data: {
        x: [
          0,
          1,
          2,
          3,
          4,
          5,
          6,
          7,
          8,
          9,
          10,
          11,
          12,
          13,
          14,
          15,
          16,
          17,
          18,
          19,
          20,
          21,
          22,
          23
        ],
        y: ['mon', 'tue', 'wed', 'thu', 'fri']
      }
    },
    KPI: {
      data: [
        {
          label: 'Groups Per Day',
          key: 'averageGroupCountsPerDayInIntervalResponse',
          avgValueKey: 'avgGroupCounts'
        },
        {
          label: 'Average Groups Size',
          key: 'avgMinMaxGroupSizeResponse',
          avgValueKey: 'avgGroupSize',
          minValueKey: 'minGroupSize',
          maxValueKey: 'maxGroupSize'
        },
        {
          label: 'Average Group Attendance Rate',
          key: 'averageParticipationPerGroupPerDay',
          avgValueKey: 'averageParticipationIngroup'
        }
      ]
    },
    Line: {
      options: {
        tooltips: {
          mode: 'index',
          intersect: false,
          filter: function(tooltipItem: any) {
            if (tooltipItem.label !== '') return tooltipItem;
          }
        },
        responsive: true,
        maintainAspectRatio: false,
        legend: {
          display: true,
          labels: {
            usePointStyle: true,
            boxWidth: 8
          }
        },
        layout: {
          padding: {
            left: 12,
            right: 0,
            top: 0,
            bottom: 12
          }
        },
        scales: {
          yAxes: [
            {
              display: false
            }
          ],
          xAxes: [
            {
              offset: true,
              ticks: {
                autoSkip: true,
                maxTicksLimit: 4,
                maxRotation: 0,
                minRotation: 0
              },
              gridLines: {
                color: 'rgba(0, 0, 0, 0)'
              }
            }
          ]
        }
      },
      data: {
        labels: [] as string[],
        datasets: [
          {
            hidden: true,
            label: 'Selected',
            key: 'selected',
            backgroundColor: chartColors[0],
            borderColor: chartColors[0],
            fill: false,
            pointRadius: 1,
            data: [] as { x: number; y: number }[]
          },
          {
            label: 'Accepted',
            key: 'accepted',
            backgroundColor: chartColors[1],
            borderColor: chartColors[1],
            fill: false,
            pointRadius: 1,
            data: [] as { x: number; y: number }[]
          },
          {
            label: 'Rejected',
            key: 'rejected',
            backgroundColor: chartColors[3],
            borderColor: chartColors[3],
            fill: false,
            pointRadius: 1,
            data: [] as { x: number; y: number }[]
          },
          {
            label: 'Not Attended',
            key: 'not_participated',
            backgroundColor: chartColors[4],
            borderColor: chartColors[4],
            fill: false,
            pointRadius: 1,
            data: [] as { x: number; y: number }[]
          },
          {
            label: 'Attended',
            key: 'participated',
            backgroundColor: chartColors[5],
            borderColor: chartColors[5],
            fill: false,
            pointRadius: 1,
            data: [] as { x: number; y: number }[]
          }
        ]
      }
    },
    Bar: {
      data: [
        {
          label: 'Selected',
          key: 'selected',
          backgroundColor: '#dfdfdf',
          borderColor: chartColors[0],
          cssClass: ['text-secondary'],
          fill: false,
          pointRadius: 1,
          value: 0
        },
        {
          label: 'Accepted',
          key: 'accepted',
          backgroundColor: '#dfdfdf',
          cssClass: ['pr-3', 'text-right'],
          borderColor: chartColors[1],
          fill: false,
          pointRadius: 1,
          value: 0
        },
        {
          label: 'Rejected',
          key: 'rejected',
          backgroundColor: '#dfdfdf',
          cssClass: ['pr-3', 'text-right'],
          borderColor: chartColors[3],
          fill: false,
          pointRadius: 1,
          value: 0
        },
        {
          label: 'Attendance',
          key: 'attendance_label',
          cssClass: ['text-secondary'],
          labelOnly: true
        },
        {
          label: 'Attended',
          key: 'participated',
          backgroundColor: '#dfdfdf',
          cssClass: ['pr-3', 'text-right'],
          borderColor: chartColors[5],
          fill: false,
          pointRadius: 1,
          value: 0
        }
      ]
    }
  };

  public selectedInterval: CalendarTimeInterval = {
    start: moment().startOf('week'),
    end: moment().endOf('week')
  };

  @Mutation
  setSelectedInterval(values: CalendarTimeInterval) {
    this.selectedInterval = values;
  }

  @Mutation
  setBarChartData(values: MetricsResponse) {
    this.chartData.Bar.data.map(slot => {
      if (slot.key === 'selected')
        slot.value = values.selectedStatusCounts
          .filter(
            x =>
              x.selectedStatus === 'accepted' || x.selectedStatus === 'rejected'
          )
          .reduce((acc: number, y) => {
            return y.counts + acc;
          }, 0);
      else if (slot.key === 'participated' || slot.key === 'not_participated') {
        let val = values.participationStatusCountsResponse.filter(
          dayParticipationStatus =>
            dayParticipationStatus.participationStatus === slot.key
        );
        if (val)
          slot.value = val.reduce((acc: number, dayParticipationStatus) => {
            return dayParticipationStatus.counts + acc;
          }, 0);
      } else {
        let val = values.selectedStatusCounts.filter(
          dayStatus => dayStatus.selectedStatus === slot.key
        );
        if (values)
          slot.value = val.reduce((acc: number, dayStatus) => {
            return dayStatus.counts + acc;
          }, 0);
      }
    });
  }

  @Mutation
  setRawChartData(data: MetricsResponse) {
    this.rawChartData = data;
  }

  @Mutation
  setLineChartData(values: MetricsResponse) {
    // get days between
    const data = { ...this.chartData.Line.data };
    let now = this.selectedInterval.start.clone();
    // get all dates between start-end
    let dates: moment.Moment[] = [];
    while (now.isSameOrBefore(this.selectedInterval.end)) {
      if (
        // only dates in values are valid
        values.selectedStatusCounts.find(
          x => x.day === now.format('YYYY-MM-DD')
        ) !== undefined
      ) {
        dates.push(now.clone());
      }
      now.add(1, 'days');
    }
    // replace labels
    data.labels = dates.map((day: moment.Moment) => day.format('MM/DD'));
    // replace data
    data.datasets.forEach(x => {
      x.data = dates.map((day: moment.Moment) => {
        let value = 0;
        if (x.key === 'selected')
          value = values.selectedStatusCounts
            .filter(
              y =>
                y.day === day.format('YYYY-MM-DD') &&
                (y.selectedStatus === 'accepted' ||
                  y.selectedStatus === 'rejected')
            )
            .reduce((acc: number, y) => {
              return y.counts + acc;
            }, 0);
        else if (['accepted', 'rejected', 'unknown'].indexOf(x.key) !== -1) {
          let val = values.selectedStatusCounts.find(
            y =>
              y.day === day.format('YYYY-MM-DD') && y.selectedStatus === x.key
          );
          if (val) value = val.counts;
        } else {
          let val = values.participationStatusCountsResponse.find(
            y =>
              y.day === day.format('YYYY-MM-DD') &&
              y.participationStatus === x.key
          );
          if (val) value = val.counts;
        }

        return {
          x: 0,
          y: value
        };
      });
      if (dates.length === 1) {
        x.data.unshift({
          x: 0,
          y: 0
        });
        x.data.push({
          x: 0,
          y: 0
        });
      }
    });

    // handle one day selection
    if (dates.length === 1) {
      data.labels.push('');
      data.labels.unshift('');
    }

    this.chartData.Line.data = data;
  }

  @Action({ rawError: true })
  getMetrics(data: { start: string; end: string }) {
    return axios
      .get(
        `/jwc/statistics/interval?startDate=${data.start}&endDate=${data.end}&timezone=${GroupsStore.selectedTimezone?.offset}&filterTimezone=${GroupsStore.selectedFilterTimezone}`
      )
      .then(response => {
        if (response.data) {
          this.setRawChartData(response.data);
          this.setBarChartData(response.data);
          this.setLineChartData(response.data);
        }
      })
      .catch(err => {
        console.log(err);
      });
  }
}
export const MetricsStore = getModule(MetricsStoreModule);
