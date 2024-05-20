import {Component, ViewChild} from '@angular/core';
import {ChartComponent} from "chart.js";


export type ChartOptions = {
  series: any;
  chart: any;
  dataLabels: any;
  plotOptions: any;
  responsive: any[];
  xaxis: any;
  legend: any;
  fill: any;
};

@Component({
  selector: 'app-stacked-columns',
  templateUrl: './stacked-columns.component.html',
  styleUrls: ['./stacked-columns.component.scss']
})
export class StackedColumnsComponent {
  @ViewChild("chart") chart!: ChartComponent;
  public chartOptions: Partial<ChartOptions>;

  constructor() {
    this.chartOptions = {
      series: [
        {
          name: "PRODUCT A",
          data: [44, 55, 41, 67, 22, 43]
        },
        {
          name: "PRODUCT B",
          data: [13, 23, 20, 8, 13, 27]
        },
        {
          name: "PRODUCT C",
          data: [11, 17, 15, 15, 21, 14]
        },
        {
          name: "PRODUCT D",
          data: [21, 7, 25, 13, 22, 8]
        }
      ],
      chart: {
        type: "bar",
        height: 200,
        stacked: true,
        toolbar: {
          show: false
        },
        zoom: {
          enabled: true
        }
      },
      responsive: [
        {
          breakpoint: 480,
          options: {
            legend: {
              position: "bottom",
              offsetX: -10,
              offsetY: 0
            }
          }
        }
      ],
      plotOptions: {
        bar: {
          horizontal: false
        }
      },
      xaxis: {
        type: "category",
        categories: [
          "Jan",
          "Feb",
          "Mar",
          "Apr",
          "May",
          "June"
        ]
      },
      legend: {
        position: "right",
        offsetY: 40
      },
      fill: {
        opacity: 1
      }
    };
  }
}
