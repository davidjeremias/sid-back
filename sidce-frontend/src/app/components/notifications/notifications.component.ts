import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { NotificationsService } from './notifications.service';
import { Message } from 'primeng/api';

@Component({
  selector: 'notifications-component',
  template: '<div toastContainer></div>'
})
export class NotificationsComponent implements OnInit {

  subscription: Subscription;

  constructor(private notificationService: NotificationsService, private messageService: ToastrService) { }

  ngOnInit() {
    this.subscribeToNotifications();
  }

  subscribeToNotifications() {
    this.subscription = this.notificationService.change
    .subscribe(notification => {
      const message: Message = notification;
      switch (message.severity) {
        case "error":
          this.messageService.error(message.detail, message.summary);
          break;
        case "warn":
          this.messageService.warning(message.detail, message.summary);
          break;
        case "success":
          this.messageService.success(message.detail, message.summary);
          break;
        default:
          this.messageService.info(message.detail, message.summary);
      }
    });
  }
  
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}
