import { Component, OnInit } from '@angular/core';
import { MatIcon } from "@angular/material/icon";
import { Route, Router } from '@angular/router';
import {WebSocketService} from '../../services/notifications.service';

@Component({
  selector: 'app-exit-button',
  templateUrl: './exit-button.component.html',
  styleUrls: ['./exit-button.component.css'],
  imports: [MatIcon]
})
export class ExitButtonComponent implements OnInit {

  constructor(private router: Router,private notificationsService: WebSocketService) { }

  ngOnInit() {
  }
  public handleExit(): void {

    this.router.navigate(['login']);
    this.notificationsService.disconnect();
  }
}
