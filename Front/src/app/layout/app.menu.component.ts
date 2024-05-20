import { OnInit } from '@angular/core';
import { Component } from '@angular/core';
import { LayoutService } from './service/app.layout.service';

@Component({
  selector: 'app-menu',
  templateUrl: './app.menu.component.html',
})
export class AppMenuComponent implements OnInit {
  model: any[] = [];

  constructor(public layoutService: LayoutService) {}

  ngOnInit() {
    this.model = [
      {

        items: [ {label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/']},
          {label: 'Users', icon: 'pi pi-fw pi-user', routerLink: ['/users']} ,
          {label: 'Rules', icon: 'pi pi-fw pi-list', routerLink: ['/rules']} ,
          {label: 'Add rule', icon: 'pi pi-fw pi-plus', routerLink: ['/addrule']},
          {label: 'Configuration table ', icon: 'pi pi-fw pi-table', routerLink: ['/configtable']},
          {label: 'Add user ', icon: 'pi pi-fw pi-user-plus', routerLink: ['/adduser']}
        ]
      } ]
  }
}
