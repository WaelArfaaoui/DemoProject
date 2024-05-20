import { Component, OnInit } from '@angular/core';
import {LayoutService} from "../../layout/service/app.layout.service";
import {Subscription} from "rxjs";
import {RuleService} from "../../../open-api";
@Component({
  selector: 'app-dashbord',
  templateUrl: './dashbord.component.html',
  styleUrls: ['./dashbord.component.scss']
})
export class DashbordComponent implements OnInit {
  pieData: any;
  lineData: any;
  lineOptions: any;
  pieOptions: any;
  polarData:any;
  polarOptions:any;
  barData:any;
  barOptions:any;
  radarData:any;
  radarOptions:any;
  subscription: Subscription;
  totalUsages!: number;
  totalRules!: number;
  constructor(public layoutService: LayoutService , public ruleService:RuleService) {
    this.subscription = this.layoutService.configUpdate$.subscribe(config => {
      this.initCharts();
    });
  }
  ngOnInit(): void {
    this.getTotalRules() ;
    this.getTotalRulesUsages() ;
    this.initCharts();
  }
  initCharts() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color');
    const textColorSecondary = documentStyle.getPropertyValue('--text-color-secondary');
    const surfaceBorder = documentStyle.getPropertyValue('--surface-border');
    this.barData = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'My First dataset',
          backgroundColor: documentStyle.getPropertyValue('--primary-500'),
          borderColor: documentStyle.getPropertyValue('--primary-500'),
          data: [65, 59, 80, 81, 56, 55, 40]
        },
        {
          label: 'My Second dataset',
          backgroundColor: documentStyle.getPropertyValue('--primary-200'),
          borderColor: documentStyle.getPropertyValue('--primary-200'),
          data: [28, 48, 40, 19, 86, 27, 90]
        }
      ]
    };
    this.barOptions = {
      plugins: {
        legend: {
          labels: {
            fontColor: textColor
          }
        }
      },
      scales: {
        x: {
          ticks: {
            color: textColorSecondary,
            font: {
              weight: 500
            }
          },
          grid: {
            display: false,
            drawBorder: false
          }
        },
        y: {
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        },
      }
    };
    this.pieData = {
      labels: ['A', 'B', 'C'],
      datasets: [
        {
          data: [540, 325, 702],
          backgroundColor: [
            documentStyle.getPropertyValue('--indigo-500'),
            documentStyle.getPropertyValue('--purple-500'),
            documentStyle.getPropertyValue('--teal-500')
          ],
          hoverBackgroundColor: [
            documentStyle.getPropertyValue('--indigo-400'),
            documentStyle.getPropertyValue('--purple-400'),
            documentStyle.getPropertyValue('--teal-400')
          ]
        }]
    };
    this.pieOptions = {
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
            color: textColor
          }
        }
      }
    };
    this.lineData = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'First Dataset',
          data: [65, 59, 80, 81, 56, 55, 40],
          fill: false,
          backgroundColor: documentStyle.getPropertyValue('--primary-500'),
          borderColor: documentStyle.getPropertyValue('--primary-500'),
          tension: .4
        },
        {
          label: 'Second Dataset',
          data: [28, 48, 40, 19, 86, 27, 90],
          fill: false,
          backgroundColor: documentStyle.getPropertyValue('--primary-200'),
          borderColor: documentStyle.getPropertyValue('--primary-200'),
          tension: .4
        }
      ]
    };
    this.lineOptions = {
      plugins: {
        legend: {
          labels: {
            fontColor: textColor
          }
        }
      },
      scales: {
        x: {
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        },
        y: {
          ticks: {
            color: textColorSecondary
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false
          }
        },
      }
    };
    this.polarData = {
      datasets: [{
        data: [
          11,
          16,
          7,
          3
        ],
        backgroundColor: [
          documentStyle.getPropertyValue('--indigo-500'),
          documentStyle.getPropertyValue('--purple-500'),
          documentStyle.getPropertyValue('--teal-500'),
          documentStyle.getPropertyValue('--orange-500')
        ],
        label: 'My dataset'
      }],
      labels: [
        'Indigo',
        'Purple',
        'Teal',
        'Orange'
      ]
    };
    this.polarOptions = {
      plugins: {
        legend: {
          labels: {
            color: textColor
          }
        }
      },
      scales: {
        r: {
          grid: {
            color: surfaceBorder
          }
        }
      }
    };
    this.radarData = {
      labels: ['Eating', 'Drinking', 'Sleeping', 'Designing', 'Coding', 'Cycling', 'Running'],
      datasets: [
        {
          label: 'My First dataset',
          borderColor: documentStyle.getPropertyValue('--indigo-400'),
          pointBackgroundColor: documentStyle.getPropertyValue('--indigo-400'),
          pointBorderColor: documentStyle.getPropertyValue('--indigo-400'),
          pointHoverBackgroundColor: textColor,
          pointHoverBorderColor: documentStyle.getPropertyValue('--indigo-400'),
          data: [65, 59, 90, 81, 56, 55, 40]
        },
        {
          label: 'My Second dataset',
          borderColor: documentStyle.getPropertyValue('--purple-400'),
          pointBackgroundColor: documentStyle.getPropertyValue('--purple-400'),
          pointBorderColor: documentStyle.getPropertyValue('--purple-400'),
          pointHoverBackgroundColor: textColor,
          pointHoverBorderColor: documentStyle.getPropertyValue('--purple-400'),
          data: [28, 48, 40, 19, 96, 27, 100]
        }
      ]
    };
    this.radarOptions = {
      plugins: {
        legend: {
          labels: {
            fontColor: textColor
          }
        }
      },
      scales: {
        r: {
          grid: {
            color: textColorSecondary
          }
        }
      }
    };
  }
  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  getTotalRules(){
    this.ruleService.getTotalRulesCount().subscribe({
      next: data => {
        this.totalRules = data;
      },
      error: error => {
        console.error('Error loading rules count:', error);
      }
    });
  }

  getTotalRulesUsages(){
    this.ruleService.getTotalRuleUsages().subscribe({
      next: data => {
        this.totalUsages = data;
      },
      error: error => {
        console.error('Error loading rules usage count :', error);
      }
    });
  }

}
