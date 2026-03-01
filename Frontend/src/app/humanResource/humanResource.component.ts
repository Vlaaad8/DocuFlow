import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatSidenavContainer, MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { Network } from 'vis-network';
import { DataSet } from 'vis-data';
import { HrService } from '../services/hr.service';
import { User } from '../model/User';
import { MatIcon } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { Relation } from '../model/Relation';
import { SnackBarService } from '../services/snackBar.service';
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

interface Node {
  id: number;
  label: string;
}
interface Edge {
  from: number,
  to: number
  arrows: string
}
@Component({
  selector: 'app-humanResource',
  templateUrl: './humanResource.component.html',
  styleUrls: ['./humanResource.component.css'],
  imports: [MatSidenavContainer, SidenavUserComponent, MatSidenavModule, ExitButtonComponent, MatIcon, CommonModule, MatDialogModule,ReactiveFormsModule]
})
export class HumanResourceComponent implements OnInit, AfterViewInit {

  private network: Network | null = null;
  private node!: DataSet<Node>;

  private users: User[] = [];
  private relations: Relation[] = [];

  public addEdgeMode: boolean = false;
  public removeEdgeMode: boolean = false;

   errorMessage: string | null = null;
   registerErrorMessage: string | null = null;

  @ViewChild('network') networkContainer!: ElementRef;
  @ViewChild('createUserDialog') createUserDialog: any;

  private loggedUser! :User;
  public registerForm!: FormGroup;

  constructor(private service: HrService,private snackBar: SnackBarService,private dialog: MatDialog,private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.loggedUser = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    this.registerForm = this.formBuilder.group({
      firstname: new FormControl('', [Validators.required]),
      lastName: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      role: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      username: new FormControl('', [Validators.required, Validators.minLength(4)])
    });
   }
  
  ngAfterViewInit(): void {

    forkJoin([
      this.service.getRelations(),
      this.service.getUsers()
    ]).subscribe(([relations, users]) => {
      this.users = users;
      this.relations = relations;
      this.buildNetwork(users, relations);
    });

  }

  private buildNetwork(users: User[], relations: Relation[]): void {
    const nodesArray = users.map(user => ({
      id: user.id,
      label: `${this.determineIcon(user.role)}`,
      title: `${user.firstName} ${user.lastName}`,
      color: {
        background: this.determineColor(user.role),
        border: this.determineBorderColor(user.id),
        highlight: {
          background: this.determineHighlightColor(user.role),
          border: '#bdc3c7'
        },
        
      },
      borderWidth: this.determineBorderWidth(user.id),
      level: this.determineLevel(user.role),

      font: {
        color: '#ffffff',
        size: 50,
        strokeWidth: 1,
        strokeColor: 'rgba(0,0,0,0.2)'
      },
      shape: 'circle',
      size: 90
    }));

    const nodes = new DataSet(nodesArray);
    const edges = new DataSet(relations.map(relation => ({
      id: `${relation.id}`,
      from: relation.subordinate.id,
      to: relation.boss.id,
      arrows: 'to'
    })))
    const data = { nodes, edges };

    const options = {
      nodes: {
        widthConstraint: { minimum: 90, maximum: 90 },
        heightConstraint: { minimum: 90}
      },
      interaction: {
        hover: true,
        tooltipDelay: 200
      },
      layout: {
        hierarchical: {
          enabled: true,
          levelSeparation: 150,
          nodeSpacing: 200,
          parentCentralization: true,
          direction: 'UD',
          sortMethod: 'directed'
        }
      },
      physics: { enabled: false },
      height: '500px',
      width: '100%',
      edges: {
        smooth: {
          enabled: true,
          type: 'cubicBezier',
          forceDirection: 'vertical',
          roundness: 0.4
        },
        arrows: { to: { enabled: true } },
        color: {
          color: '#bdc3c7',
          highlight: '#3b82f6',
          hover: '#4338ca',
          inherit: false
        }
      },
      manipulation: {
        enabled: true,
        addEdge: (data: Edge, callback: any) => {
          if (data.from === data.to) {
            callback(null);
          }
          else {
            data.arrows = 'to';
            this.service.addRelation(data.to, data.from).subscribe({
              next: () => {
                callback(data)
                this,this.snackBar.showMessage("Relation added successfully!", "success");
              },
              error: (error) => {
                console.log(error);
                this.errorMessage = error.error || 'An error occurred while adding the relation. Please try again.';
                callback(null);
              }
            });
            this.addEdgeMode = false;
          }
        }
      }

    };


    if (this.networkContainer) {
      this.network = new Network(this.networkContainer.nativeElement, data, options);
    }
  }
  public determineColor(role: string): string {

    if (role === "CEO") {
      return '#e74c3c';
    } else if (role === "Manager") {
      return '#2ecc71';
    } else if (role === "HR") {
      return '#9b59b6';
    } else if (role === "IT") {
      return '#3498db';
    } else if (role === "Finance") {
      return '#22c55e';
    } else if (role === "Law") {
      return '#eab308';
    } else if (role === "Sales") {
      return '#f5c888';
    } else if (role === "Support") {
      return '#1bbcd8';
    } else if (role === "Marketing") {
      return '#f97316';
    } else if (role === "Employee") {
      return '#95a5a6';
    }

    return '#95a5a6';
  }

  public determineIcon(role: string): string {
    switch (role) {
      case "CEO": return '👑';
      case "Manager": return '🧑‍💼';
      case "HR": return '🧑‍🤝‍🧑';
      case "IT": return '💻';
      case "Finance": return '💰';
      case "Law": return '⚖️';
      case "Sales": return '📈';
      case "Support": return '🎧';
      case "Marketing": return '📢';
      case "Employee": return '🧑‍🔧';
      default: return '🧑‍🔧';
    }
  }

  public determineHighlightColor(role: string): string {
    switch (role) {
      case "CEO": return '#b03a2e';
      case "Manager": return '#1e8449';
      case "HR": return '#7d3c98';
      case "IT": return '#21618c';
      case "Finance": return '#1b5e20';
      case "Law": return '#9a7d0a';
      case "Sales": return '#af601a';
      case "Support": return '#0e6675';
      case "Marketing": return '#a04000';
      case "Employee": return '#515a5a';
      default: return '#515a5a';
    }
  }

  public determineLevel(role: string): number {
    if (role === "CEO") {
      return 0;
    }
    else if (role === "Manager") {
      return 1;
    }
    else if (role === "HR" || role === "IT" || role === "Finance" || role === "Law") {
      return 2;
    }
    else if (role === "Marketing" || role === "Sales") {
      return 3;
    }

    return 4;
  }
  toggleAddEdge(): void {
    this.addEdgeMode = !this.addEdgeMode;
    this.removeEdgeMode = false;
    this.network?.addEdgeMode();
    console.log("Add edge mode:", this.addEdgeMode);

  }
  toggleRemoveEdge(): void {
    this.removeEdgeMode = !this.removeEdgeMode;
    this.addEdgeMode = false;
  }


  determineBorderColor(userID:number): string {
    if (userID == this.loggedUser.id) {
      return '#facc15';; // Blue border for logged-in user
    }
    return '#95a5a6'; // Default border color
  }
  determineBorderWidth(userID:number): number {
    if (userID == this.loggedUser.id) {
      return 5; 
    }
   return 2;
  }

   openCreateModal(): void {
    this.dialog.open(this.createUserDialog);
  }
  closeCreateModal(): void {
    this.dialog.closeAll();
  }
  registerUser(): void {
    if (this.registerForm.valid) {
      const formData = this.registerForm.value;
     
      console.log('Registering user with data:', formData);
      this.closeCreateModal();
      this.service.addUser(formData.firstname, formData.lastName, formData.email, formData.role, formData.password, formData.username).subscribe({
        next: () => {
          this.snackBar.showMessage("User registered successfully!", "success");
          this.ngAfterViewInit();
        },
        error: (error) => {
          console.error('Error registering user:', error);
          this.registerErrorMessage = error.error || 'An error occurred while registering the user. Please try again.';
        }
      });
     this.snackBar.showMessage("Error registering user!", "error");
    }
  }




}