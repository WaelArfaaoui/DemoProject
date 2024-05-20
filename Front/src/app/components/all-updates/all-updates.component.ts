import { Component, OnInit } from '@angular/core';
import {RuleDto, RuleModificationDto, RuleService} from '../../../open-api';
import {UseRuleComponent} from "../use-rule/use-rule.component";
import {DialogService} from "primeng/dynamicdialog";

@Component({
  selector: 'app-all-updates',
  templateUrl: './all-updates.component.html',
  styleUrls: ['./all-updates.component.scss']
})
export class AllUpdatesComponent implements OnInit {

  updates: RuleModificationDto[] = [];
  displayedUpdatesCount = 6;  // Initial number of updates to display
  private selectedRule!: RuleDto;

  constructor(private ruleService: RuleService , private dialogService:DialogService) { }

  ngOnInit(): void {
    this.getAllUpdates();
  }

  getAllUpdates(): void {
    this.ruleService.getAllUpdates().subscribe(
        data => {
          this.updates = data;
        },
        error => {
          console.error('Error fetching updates:', error);
        }
    );
  }

  loadMoreUpdates(event: Event): void {
    event.preventDefault();  // Prevent the default anchor behavior
    this.displayedUpdatesCount += 6;  // Increment the number of updates to display by 6
  }

  useRule(ruleId: number | undefined) {
    if (ruleId === undefined) {
      console.error("Rule ID is undefined");
      return;
    }

    this.ruleService.findRuleById(ruleId).subscribe(
        data => {
          this.selectedRule = data;
          const headerText = this.selectedRule.status == 'Enabled' ? 'Use Rule' : 'Rule';

          const ref = this.dialogService.open(UseRuleComponent, {
            header: headerText,
            width: '900px',
            height: '600px',
            contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"},
            data: this.selectedRule
          });
        },
        error => {
          console.error("Error fetching rule:", error);
        }
    );
  }

}
