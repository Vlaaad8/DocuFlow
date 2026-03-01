import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Subject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

  private client!: Client;
  private notificationSubject = new Subject<any>();

  connect(userId: string): void {
    this.client = new Client({
      brokerURL: `ws://localhost:8080/ws?userId=${userId}`,

      onConnect: () => {
        console.log('WebSocket conectat!');
        this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
          const data = JSON.parse(message.body);
          this.notificationSubject.next(data);
        });
      },

      onDisconnect: () => console.log('WebSocket deconectat!'),
      onStompError: (frame) => console.error('Eroare:', frame),
      reconnectDelay: 5000,
    });

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
  }

  getNotifications(): Observable<any> {
    return this.notificationSubject.asObservable();
  }
}