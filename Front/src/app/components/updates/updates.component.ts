import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from "@angular/router";
import { UserService } from "../../services/user/user.service";
import { RuleModificationDto, RuleService, UserControllerService } from "../../../open-api";
import { UseRuleComponent } from "../use-rule/use-rule.component";
import { DialogService } from "primeng/dynamicdialog";

@Component({
    selector: 'app-updates',
    templateUrl: './updates.component.html',
    styleUrls: ['./updates.component.scss']
})
export class UpdatesComponent implements OnInit, OnDestroy {

    username: any;
    role: any;
    profileImagePath: string | undefined;
    updates: RuleModificationDto[] = [];
    private selectedRule: any;
    private updateInterval: any;

    constructor(
        private router: Router,
        private userService: UserService,
        private userControllerService: UserControllerService,
        private ruleService: RuleService,
        private dialogService: DialogService
    ) { }

    ngOnInit(): void {
        this.getUserByEmail();
        this.getAllUpdates();
        // Reload updates every minute
        this.updateInterval = setInterval(() => {
            this.getAllUpdates();
        }, 60000);
    }

    ngOnDestroy(): void {
        if (this.updateInterval) {
            clearInterval(this.updateInterval);
        }
    }

    getUserByEmail() {
        const email = this.userService.getUserDetails().email;
        if (email) {
            this.userControllerService.getUser(email).subscribe(
                (user) => {
                    this.username = user.firstname;
                    this.role = user.role;
                    this.profileImagePath = user.profileImagePath;

                    // Serialize user details to JSON string
                    const userDetailsJSON = JSON.stringify({
                        username: this.username,
                        role: this.role,
                        profileImagePath: this.profileImagePath
                    });

                    // Store user details in local storage
                    localStorage.setItem('userDetails', userDetailsJSON);
                },
                (error) => {
                    console.error("Error fetching user:", error);
                }
            );
        } else {
            console.error("Email is null or not found in localStorage");
        }
    }

    isDarkTheme = false;

    toggleDarkTheme(): void {
        console.log("clicked");
        this.isDarkTheme = !this.isDarkTheme;
        document.body.classList.toggle('dark-theme-variables', this.isDarkTheme);
    }

    isSettingsDiplayed = false;

    displaySettings() {
        console.log("clicked");
        this.isSettingsDiplayed = !this.isSettingsDiplayed;
    }

    onNavigateToProfile() {
        this.router.navigate(['/profile']);
        this.isSettingsDiplayed = !this.isSettingsDiplayed;
    }

    getAllUpdates() {
        this.ruleService.getAllUpdates().subscribe(
            data => {
                this.updates = data.slice(0, 6);
                console.log(this.updates);
            },
            error => {
                console.error("Error fetching updates:", error);
            }
        );
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
                    contentStyle: { "background-color": "var(--color-white)", "color": "var(--color-dark)" },
                    data: this.selectedRule
                });
            },
            error => {
                console.error("Error fetching rule:", error);
            }
        );
    }
}
