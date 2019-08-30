import { Injectable, Output, EventEmitter } from "@angular/core";
import { Message } from "primeng/api";

@Injectable()
export class NotificationsService {
    
  @Output() change: EventEmitter<Message> = new EventEmitter();

  add(message: Message) {
    this.change.emit(message);
  }

}