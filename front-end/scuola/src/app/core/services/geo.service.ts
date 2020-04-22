import { Injectable } from '@angular/core'
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { of, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';


@Injectable()
export class GeoService {
  timeout: number = 60000;
  
  constructor(private http: HttpClient) {}

  getAddressFromCoordinates(location): Promise<any> {
    var url = 'https://os.smartcommunitylab.it/core.geocoder/location?latlng=' + location.latlng.lat + ',' + location.latlng.lng;
    environment.globalSpinner = false;
    return this.http
      .get(url)
      .timeout(this.timeout)
      .toPromise()
      .then(response => {
        environment.globalSpinner = true;
        let places = response["response"].docs;
        let name = '';
        if (places[0]) {
          return this.getNameFromComplex(places[0])
        }
        return null;
      })
      .catch(this.handleError);
  }

  getLocations(location: string): Observable<any> {
    if (location === '') {
      return of([]);
    }

    var url = 'https://os.smartcommunitylab.it/core.geocoder/address?address=' + location;
    environment.globalSpinner = false;
    return this.http.get<any>(url, { observe: 'response' }).timeout(this.timeout)
      .map(result => {
        environment.globalSpinner = true;
        if (result.body && result.body.response && result.body.response.docs) {
          return (result.body.response.docs);
        }
      })
      .catch(this.handleError);
  }

  createPlaces = function (places) {
    let geoCoderPlaces = [];
    
    if (places) {
      for (var i = 0; i < places.length; i++) {
        let temp = '';
        if (places[i].name)
          temp = temp + places[i].name;
        if (places[i].street != places[i].name)
          if (places[i].street) {
            if (temp)
              temp = temp + ', ';
            temp = temp + places[i].street;
          }
        if (places[i].housenumber) {
          if (temp)
            temp = temp + ', ';
          temp = temp + places[i].housenumber;
        }
        if (places[i].city) {
          if (temp)
            temp = temp + ', ';
          temp = temp + places[i].city;
        }
        if (places[i].state) {
          temp = temp + ' - ' + places[i].state;
        }
        geoCoderPlaces[i] = {
          name: temp,
          location: places[i].coordinate.split(',')
        }
      }
    }

    return geoCoderPlaces;
  }

  getNameFromComplex = function (data) {
    let name = '';
    if (data) {
      if (data.name) {
        name = name + data.name;
      }
      if (data.street && (data.name != data.street)) {
        if (name)
          name = name + ', ';
        name = name + data.street;
      }
      if (data.housenumber) {
        if (name)
          name = name + ', ';
        name = name + data.housenumber;
      }
      if (data.city) {
        if (name)
          name = name + ', ';
        name = name + data.city;
      }
      return name;
    }
  }

  private handleError(error: HttpErrorResponse) {
    let errMsg = "Errore del server! Prova a ricaricare la pagina.";

    if (error.name === 'TimeoutError') {
      errMsg = error.message;
    }
    else if (error.error) {
      if (error.error.ex) {
        errMsg = error.error.ex;
        // Use the following instead if using lite-server
        //return Observable.throw(err.text() || 'backend server error');
      } else if (typeof error.error === "string") {
        try {
          let errore = JSON.parse(error.error);
          if (errore.ex) {
            errMsg = errore.ex;
          }
        }
        catch (e) {
          console.error('server error:', errMsg);
        }
      }
    }

    environment.globalSpinner = true;
    console.error('server error:', errMsg);
    
    return Observable.throw(errMsg);
  }
}
