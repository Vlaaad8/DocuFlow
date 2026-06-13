# DocuFlow 📄🔐

> **Un sistem complet de management și semnare digitală a documentelor cu flux de aprobare automatizat.**

DocuFlow este o platformă enterprise care centralizează gestionarea documentelor, permite crearea de template-uri reutilizabile și implementează fluxuri de aprobare cu semnare digitală.

---

## 🎯 Ce rezolvă DocuFlow

- 📋 **Centralizare documente** — toate documentele importante într-un loc sigur și organizat
- ✍️ **Semnare digitală** — documente PDF semnate electronic cu certificat digital
- 🔄 **Flux de aprobare** — lanțuri de aprobare configurabile după rol (Manager, Finance, HR, Law, CEO)
- 📝 **Template-uri documente** — creare și reutilizare de template-uri cu câmpuri predefinite
- 🧠 **Extragere automată date (OCR)** — scanare și extragere de date din ID-uri și documente
- 👥 **Gestiune useri și roluri** — control granular per rol: Employee, Manager, Finance, HR, Law, IT, CEO, Marketing, Sales, Support
- 📊 **Istoric și audit trail** — înregistrare completă a tuturor acțiunilor (cine, ce, când)
- 🔔 **Notificări** — informare în timp real a participanților despre pași în așteptare
- 📈 **Dashboard analytics** — vizualizare status documente, grafice și statistici

---

## 🏗️ Arhitectura

DocuFlow este o aplicație **multi-modul** cu separare netă între frontend și backend:

### Backend (Java Spring Boot)
```
Domain/          — Entități JPA și DTOs (User, Template, ApprovalChain, FilledTemplate)
Web/             — REST Controllers (OCR, Login, Templates, Approvals)
Signature/       — Modul semnare digitală (PAdES, certificare)
Infrastructure/  — Data Access Layer (Repositories, JPA)
```

**Stack backend:**
- Java 21
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- Digital Signature Service (EU DSS library)
- PDF manipulation (Apache PDFBox)
- Lombok

### Frontend (Angular 20)
```
Frontend/
├── src/app/
│   ├── dashboard/          — Dashboard principal
│   ├── login/              — Autentificare
│   ├── templates/          — Vizualizare și management template-uri
│   ├── template-creator/   — Editor template-uri cu drag-drop câmpuri
│   ├── upload/             — Upload documente
│   ├── generate/           — Generare documente din template-uri
│   ├── approvalFlow/       — Configurare lanțuri de aprobare
│   ├── requests/           — Gestionare cereri de aprobare
│   ├── humanResource/      — Management HR (useri, relații boss-subordinat)
│   ├── my-profile/         — Profil utilizator
│   ├── services/           — HTTP Services (API communication)
│   └── model/              — TypeScript interfaces
```

**Stack frontend:**
- Angular 20.3.0
- Angular Material
- TypeScript 5.9
- RxJS
- Chart.js & D3.js (vizualizări)
- TinyMCE (editor rich text)
- PDF Viewer (ng2-pdf-viewer)
- WebSocket (STOMP) pentru notificări real-time

---

## 🔑 Funcționalități principale

### 1. **Gestionare Template-uri**
- Creație template-uri cu categorii (Contract, HR, Finance, etc.)
- Definire câmpuri dinamice (text, date, checkbox, select)
- Asociere lanț de aprobare pe template
- Reutilizare template-uri pentru documente noi

### 2. **Semnare Digitală (PAdES)**
- Semnare PDF cu certificat digital (PKCS#12)
- Suport pentru semnare în lanț (mai mulți semnatari)
- Validare și verificare semnături digitale
- Compliance EU DSS (Digital Signature Service)

### 3. **Flux de Aprobare Multi-nivel**
```
ApprovalChain {
  id: number
  name: string
  steps: [
    { stepNumber: 1, approverRole: "Manager" },
    { stepNumber: 2, approverRole: "Finance" },
    { stepNumber: 3, approverRole: "CEO" }
  ]
}
```
- Ordinea de aprobare configurabilă
- Aprobări pe rol, nu pe persoană
- Notificări automate la fiecare pas
- Status tracking: Pending, Approved, Rejected

### 4. **Extragere Date (OCR)**
- Scanare ID-uri (extragere: Nume, Prenume, Nr. Document, Data Expirare, etc.)
- Mapare automată a datelor pe câmpuri template
- Stocare valori pentru refolosire rapidă

### 5. **Gestiune Utilizatori**
- Criere useri cu rol și email
- Relații organizaționale (boss-subordinat)
- Gestiune certificate digitale per utilizator
- Profil utilizator și stocare valori OCR

### 6. **Dashboard Analytics**
- Vizualizare documente pendinge
- Status flux de aprobare per document
- Grafice și statistici (status, timeline, etc.)
- Audit trail complet

---

## 📱 Fluxul utilizator

### Scenariu 1: Contract cu semnare multiplă
```
1. Admin încarcă template "Contract Service"
2. HR generează document din template și completează date
3. HR selectează destinatari: Manager → Finance → CEO
4. Sistem notifică Manager (email, dashboard)
5. Manager aprobă → trece la Finance
6. Finance aprobă → trece la CEO
7. CEO semnează digital (validează certificat)
8. Sistem arhivează PDF semnat final
9. HR descarcă document final semnat
```

### Scenariu 2: Extragere date din ID
```
1. Utilizator încarcă scan ID via OCR
2. Sistem extrage: Nume, Prenume, Document Nr., Expirare
3. Date stocate cu confidence score
4. Utiliz. complet template "Employee Onboarding" cu date OCR pre-populate
5. Trimite spre HR pentru aprobă
6. HR aprobă → CEO semnează
```

---

## 🚀 Cum să pornești

### Cerințe
- Java 21+
- Node.js 20+
- Angular CLI 20
- Database: PostgreSQL/MySQL
- Certificate digital (PKCS#12) pentru semnare

---

## 🔐 Roluri și Permisiuni

| Rol | Permisiuni |
|-----|-----------|
| **Employee** | Vizualizează proprie documente, generează din template |
| **Manager** | Aprobă documente, gestionează echipă, vede analytics |
| **Finance** | Aprobă documente financiare, gestionează buget |
| **HR** | Gestiune useri, template-uri HR, semnare documente |
| **Law** | Aprobă documente legale, gestionează contracte |
| **IT** | Configurare sistem, backup, securitate |
| **CEO** | Aprobare finală, acces complet audit trail |
| **Marketing, Sales, Support** | Roluri specifice departament |

---

## 📦 Dependențe importante

### Backend
- **Spring Data JPA** — ORM și repository pattern
- **EU DSS (Digital Signature Service)** — semnare digitală conformă EIDAS
- **Apache PDFBox** — manipulare PDF
- **Lombok** — reducere boilerplate code

### Frontend
- **Angular Material** — UI components profesionale
- **Chart.js** — grafice analytics
- **D3.js** — vizualizări complexe
- **ng2-pdf-viewer** — render PDF în browser
- **STOMP (WebSocket)** — notificări real-time

---

## 🎨 Interfață

- **Dashboard** — overview documente, statistici, grafice
- **Template Creator** — editor WYSIWYG pentru template-uri cu drag-drop câmpuri
- **Approval Flow Builder** — configurare vizuală lanț de aprobare
- **Document Viewer** — preview PDF inline în browser
- **User Management** — tabel utilizatori, relații organizaționale
- **Analytics** — dashboard cu status, timeline, top approvers

---

## 🔒 Securitate

- ✅ Semnare digitală conformă **EIDAS** (EU regulation)
- ✅ Certificate digitale **PKCS#12** per utilizator
- ✅ Audit trail complet (cine, ce, când)
- ✅ Control acces pe rol
- ✅ Hashing parolă (Spring Security)
- ✅ CORS configurat
- ✅ Storage document pe disk/cloud

---

## 📝 Status proiect

DocuFlow este în **dezvoltare activă** cu următoarele module implementate:
- ✅ Autentificare și gestiune useri
- ✅ Template-uri și câmpuri dinamice
- ✅ Fluxuri de aprobare cu step-uri pe rol
- ✅ Semnare digitală PAdES
- ✅ OCR pentru ID-uri
- ✅ Dashboard cu analytics
- ✅ WebSocket notificări real-time
- 🚧 Rapoarte avansate
- 🚧 Integrare e-mail pentru notificări
- 🚧 Export/Import bulk template-uri

---
