export interface ApprovalChainStep {
  id: number,
  stepNumber: number,
  approverRole: string,
}

export interface ApprovalChain {
  id: number,
  name: string,
  steps: ApprovalChainStep[]
}

export interface Approval {
  id: number;
  status: string;
  requesterName: string;
  templateName: string;
  decisionDate: string;
  documentPath: string;
}

export interface ApprovalDetails {
  id: number;
  status: string;
  decisionDate: string;
  approverName: string;
  approverRole: string;
}

export interface ApprovalRequest {
  id: number;
  templateTitle: string;
  templateType: string;
  status: string;
  approvals: ApprovalDetails[];
}
