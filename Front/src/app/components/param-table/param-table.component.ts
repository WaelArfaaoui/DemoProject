
import {Component, Input} from '@angular/core';
import { MessageService } from 'primeng/api';
import { HttpClient } from '@angular/common/http';
import {DeleteParamComponent} from "../delete-param/delete-param.component";
import {DialogService} from "primeng/dynamicdialog";
import {TableInfo} from "../../model/table-info";
import {TableService} from "../../services/table/table.service";
import {ParamHistoryComponent} from "../param-history/param-history.component";
@Component({
  selector: 'app-param-table',
  templateUrl: './param-table.component.html',
  styleUrls: ['./param-table.component.scss']
})
export class ParamTableComponent {

  @Input() table: TableInfo=new TableInfo();
  constructor(private messageService: MessageService, private tableService: TableService, private http: HttpClient,private dialogService:DialogService) {}
  getDataTable(table: TableInfo) {
    table.data=[];
    table.totalPageCount = Math.ceil(table.totalRows / table.limit);
    table.offset = (table.currentPage - 1) * table.limit;
    if (table.selectedColumns === []) {
      table.selectedColumns = table.columns.map(column => column.name);
    }

    this.tableService.getDataFromTable(table).subscribe({
      next:(DataFromTable)=>{table.data = DataFromTable.data;
        table.deleteRequests = DataFromTable.deleteRequests;
        table.updateRequests = DataFromTable.updateRequests;
        console.log(table.updateRequests)
       },
      error:()=>{this.messageService.add({ severity: 'error', summary: 'error data', detail: `Data loaded for ${table.name}` });},
    })

  }
  cancelUpdateInstance(table:TableInfo,primaryKeyValue :string){
    this.tableService.cancelUpdateInstance(table.name,primaryKeyValue).subscribe({
      next: (response) => {
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Deletion Cancelled',detail: response.success });
          this.getDataTable(table)
        } else {
          this.messageService.add({ severity: 'error', summary: 'Deletion not Cancelled',detail: response.error });
        }
      },
      error: (error) => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Deletion not Cancelled', detail: error});

      }
    });
  }

  canceldeletion(table:TableInfo, primaryKeyValue: string) {
    this.tableService.cancelDeletion(table.name, primaryKeyValue).subscribe({
      next: (response) => {
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Deletion Cancelled',detail: response.success });
           this.getDataTable(table)
        } else {
          this.messageService.add({ severity: 'error', summary: 'Deletion not Cancelled',detail: response.error });
        }
      },
      error: (error) => {
        console.error(error);
        this.messageService.add({ severity: 'error', summary: 'Deletion not Cancelled', detail: error});

      }
    });
  }

  updateEditedValue(table: TableInfo, row: any, column: string, newValue: any) {
    const rowId = row[table.pk.name];
    if (!table.editedValue) {
      table.editedValue = {};
    }
    if (!table.editedValue[rowId]) {
      table.editedValue[rowId] = {};
    }
    table.editedValue[rowId][column] = newValue;
  }
  isRowMarkedForUpdate(table: TableInfo, row: any): boolean {
    const rowId = row[table.pk.name];
    let rowMarkedForUpdate = false;
    for (const request of table.updateRequests) {

      if (request=== rowId) {
        rowMarkedForUpdate = true;
        break;
      }
    }
    return rowMarkedForUpdate;
  }
  isRowMarkedForDeletion(table: TableInfo, row: any): boolean {
    const rowId = row[table.pk.name];
    let rowMarkedForDeletion = false;
    for (const request of table.deleteRequests) {

      if (request=== rowId) {
        rowMarkedForDeletion = true;
        break;
      }
    }
    return rowMarkedForDeletion;
  }


  createInstanceDataUpdate(row: any, table: TableInfo) {
    const instanceData: { [column: string]: any } = {};
    const rowId = row[table.pk.name];
    instanceData[table.pk.name] = rowId;
    for (const column of table.selectedColumns) {
      if (column === table.pk.name) {
        continue;
      }
        if (table.editedValue && table.editedValue[rowId] && table.editedValue[rowId][column] !== undefined) {
          instanceData[column] = table.editedValue[rowId][column];
        }
      }

    return instanceData;
  }

editValue (table: TableInfo, row: any) {
  const instanceData = this.createInstanceDataUpdate(row, table);
  this.tableService.updateInstance(instanceData, table.name).subscribe({
  next: (response : any) =>
  {
    this.getDataTable(table);
    this.messageService.add({
      severity: 'success',
      summary: 'Parameter EDITED',
      detail: `Parameter EDITED to ${table.name}`
    });
  },
    error :

  (error: any) => {
    console.error('Error adding instance:', error);
  }
});
}

  toggleEditMode(row: any) {
    row.editMode = !row.editMode;
  }
  dblclickeditmode(row: any) {
    if (!row.editMode) {
      row.editMode = true;
    }
  }
  getColumnType(column: string): string  {
    for (let col of this.table.columns) {
      if (col.name === column) {
        return col.type;
      }
    }
    return "string";
  }

  addNewRow(table: TableInfo) {
    const newRow: { [anycolumn: string]: any } = {};
    for (let column of table.selectedColumns) {
      newRow[column] = '';
    }
    table.newRows.push(newRow);
    table.showNewRow = true;
  }
  changeLimit(newLimit: number) {
    this.table.limit = newLimit;
    this.getDataTable(this.table);
  }



  toggleplusMode(table: TableInfo, newRow: any) {
    const index = table.newRows.indexOf(newRow);
    if (index !== -1) {
      table.newRows.splice(index, 1);
    }

  }

  deleteinstance(table:TableInfo, primaryKeyValue: string) {
    this.dialogService.open(DeleteParamComponent, {
      header: 'Delete Parameter',
      width: '500px',
      contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"}
    });
    this.tableService.dataDeleteInstance = {
      table: table,
      primaryKeyValue: primaryKeyValue
    };

  }

  createInstanceData(newRow: { [column: string]: string }, table: TableInfo) {
    const instanceData: { [column: string]: string } = {};
    for (let column of table.selectedColumns) {
      instanceData[column] = newRow[column];
    }
    return instanceData;
  }

  addNewInstance(table: TableInfo, newRow: any) {
    const instanceData = this.createInstanceData(newRow, table);
    this.tableService.addInstance(instanceData, table.name).subscribe({
      next: (response: any) => {
          this.getDataTable(table);
          this.messageService.add({
            severity: 'success',
            summary: 'Parameter Added',
            detail: `Parameter added to ${table.name}`
          });
          const index = table.newRows.indexOf(newRow);
          if (index !== -1) {
            table.newRows.splice(index, 1);
          }
        },
    error: (error :any) =>
    {
      console.error('Error adding instance:', error);
    }
  });
  }

  changePage(table: TableInfo, pageNumber: number) {
    if (pageNumber >= 1 && pageNumber <= table.totalPageCount) {
      table.currentPage = pageNumber;

      table.offset = (table.currentPage - 1) * table.limit;

      this.getDataTable(table);
    }
  }
  openparamhistory(tableName: string) {
    this.dialogService.open(ParamHistoryComponent, {
      header: `History ${tableName} `,
      width: '90%',
      contentStyle: {"background-color": "var(--color-white)", "color": "var(--color-dark)"},
      data: {
        tableName: tableName
      }
    });
  }

}
