import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatSidenavContainer, MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { Network } from 'vis-network';
import { DataSet } from 'vis-data';
import { HrService } from '../services/hr.service';
import { User } from '../model/User';

interface Node {
  id: number;
  label: string;
}

@Component({
  selector: 'app-humanResource',
  templateUrl: './humanResource.component.html',
  styleUrls: ['./humanResource.component.css'],
  imports: [MatSidenavContainer, SidenavUserComponent, MatSidenavModule, ExitButtonComponent]
})
export class HumanResourceComponent implements OnInit, AfterViewInit {

  private network: Network | null = null;
  private node!: DataSet<Node>;
  private users: User[] = [];

  @ViewChild('network') networkContainer!: ElementRef;

  constructor(private service: HrService) { }

  ngOnInit() {
    this.initializeNodes();
  }
  ngAfterViewInit(): void {

    const edges = new DataSet([
     
    ]);

    const data = { nodes: this.node, edges: edges };

    const options = {
      nodes: {
        shape: 'circle',
        color: '#007bff',
        font: { color: '#ffffff' },
      },
      physics: {
        enabled: true
      },
      height: '500px'
    }

    this.network = new Network(this.networkContainer.nativeElement, data, options);
  }

  public determineColor(role: string): string {
    return '';

  }

  public initializeNodes(): void {
    this.service.getUsers().subscribe(users => {
      this.users = users;
      console.log(users);
      const nodes: Node[] = users.map(user => ({
        id: user.id,
        label: `${user.firstName} ${user.lastName}\n(${user.role})`
      }));
      this.node = new DataSet(nodes);
    });
  }


}