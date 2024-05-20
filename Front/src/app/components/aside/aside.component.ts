import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-aside',
  templateUrl: './aside.component.html',
  styleUrls: ['./aside.component.scss']
})
export class AsideComponent implements OnInit {

  items = [
    {id: 0, icon: 'home', name: 'Dashboard', route: ''},
    {id: 1, icon: 'group', name: 'Users', route: '/users'},
    {id: 2, icon: 'edit_note', name: 'Rules', route: '/rules'},
    {id: 3, icon: 'add', name: 'Add Rule', route: '/addrule'},
    {id: 4, icon: 'table', name: 'Param table', route: '/configtable'},
    {id: 5, icon: 'person_add', name: 'Add User', route: '/adduser'},
    {id: 6, icon: 'update', name: 'Updates', route: '/updates'}
  ];

  selectedItemId: number = 0;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.router.events.pipe(
        filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const navigationEnd = event as NavigationEnd; // Type assertion
      this.updateSelectedItem(navigationEnd.url);
    });
  }

  updateSelectedItem(url: string): void {
    const selectedItem = this.items.find(item => item.route === url);
    if (selectedItem) {
      this.selectedItemId = selectedItem.id;
    } else {
      this.selectedItemId = 0; // Default to Dashboard if route not found
    }
  }

  selectItem(itemId: number): void {
    let route: string;

    switch (itemId) {
      case 0:
        route = '';
        break;
      case 1:
        route = '/users';
        break;
      case 2:
        route = '/rules';
        break;
      case 3:
        route = '/addrule';
        break;
      case 4:
        route = '/configtable';
        break;
      case 5:
        route = '/adduser';
        break;
      case 6:
        route = '/updates';
        break;

      default:
        route = '/';
    }

    this.router.navigate([route]);
    this.selectedItemId = itemId;
  }

  logout(): void {
    console.log("logged out");
    localStorage.clear();
    this.router.navigate(['/signIn']);
  }
}
